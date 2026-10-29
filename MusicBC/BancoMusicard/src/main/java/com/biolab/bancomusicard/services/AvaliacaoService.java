package com.biolab.bancomusicard.services;

import com.biolab.bancomusicard.DTOs.AvaliacaoRequest;
import com.biolab.bancomusicard.DTOs.AvaliacaoResponse;
import com.biolab.bancomusicard.entities.Album;
import com.biolab.bancomusicard.entities.Artista;
import com.biolab.bancomusicard.entities.Avaliacao;
import com.biolab.bancomusicard.entities.Musica;
import com.biolab.bancomusicard.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;

@Service
public class AvaliacaoService {

    private static final String CAPA_URL = "https://coverartarchive.org/release-group/%s/front-250";

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArtistaRepository artistaRepository;
    private final AlbumRepository albumRepository;
    private final MusicaRepository musicaRepository;
    private final MusicBrainzService musicBrainz;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            UsuarioRepository usuarioRepository,
                            ArtistaRepository artistaRepository,
                            AlbumRepository albumRepository,
                            MusicaRepository musicaRepository,
                            MusicBrainzService musicBrainz) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.artistaRepository = artistaRepository;
        this.albumRepository = albumRepository;
        this.musicaRepository = musicaRepository;
        this.musicBrainz = musicBrainz;
    }

    @Transactional
    public AvaliacaoResponse avaliar(Integer usuarioId, AvaliacaoRequest req) {
        if (req.nota() == null || req.nota() < 0.5f || req.nota() > 5f) {
            throw new IllegalArgumentException("A nota deve estar entre 0.5 e 5.");
        }
        if (req.resenha() != null && req.resenha().length() > 1000) {
            throw new IllegalArgumentException("A resenha pode ter no máximo 1000 caracteres.");
        }
        String tipo = req.tipo() == null ? "" : req.tipo().toUpperCase();

        Avaliacao av;
        if (tipo.equals("MUSICA")) {
            Musica musica = garantirMusica(req.mbid());
            av = avaliacaoRepository.findByUsuarioIdAndMusicaId(usuarioId, musica.getId())
                    .orElseGet(Avaliacao::new);
            av.setMusica(musica);
        } else if (tipo.equals("ALBUM")) {
            Album album = garantirAlbum(req.mbid());
            av = avaliacaoRepository.findByUsuarioIdAndAlbumId(usuarioId, album.getId())
                    .orElseGet(Avaliacao::new);
            av.setAlbum(album);
        } else {
            throw new IllegalArgumentException("tipo deve ser MUSICA ou ALBUM.");
        }

        av.setUsuario(usuarioRepository.getReferenceById(usuarioId));
        av.setNota(req.nota());
        av.setResenha(req.resenha());
        av = avaliacaoRepository.save(av);

        boolean musica = av.getMusica() != null;
        return new AvaliacaoResponse(
                av.getId(),
                musica ? "MUSICA" : "ALBUM",
                musica ? av.getMusica().getMbid() : av.getAlbum().getMbid(),
                musica ? av.getMusica().getNome() : av.getAlbum().getNome(),
                musica ? av.getMusica().getArtista().getNome() : av.getAlbum().getArtista().getNome(),
                av.getNota(),
                av.getResenha());
    }

    private Album garantirAlbum(String mbid) {
        return albumRepository.findByMbid(mbid).orElseGet(() -> {
            JsonNode rg = musicBrainz.detalharAlbum(mbid);
            Album album = new Album();
            album.setArtista(garantirArtista(rg));
            album.setNome(rg.path("title").asText());
            album.setMbid(mbid);
            album.setDuracaoSegundos(0); // MusicBrainz não informa duração do álbum
            album.setCapaUrl(CAPA_URL.formatted(mbid));
            return albumRepository.save(album);
        });
    }

    private Musica garantirMusica(String mbid) {
        return musicaRepository.findByMbid(mbid).orElseGet(() -> {
            JsonNode rec = musicBrainz.detalharMusica(mbid);
            String rgId = rec.path("releases").path(0).path("release-group").path("id").asText("");
            if (rgId.isBlank()) {
                throw new IllegalArgumentException("Essa gravação não pertence a nenhum álbum.");
            }
            Album album = garantirAlbum(rgId);

            Musica m = new Musica();
            m.setNome(rec.path("title").asText());
            m.setMbid(mbid);
            m.setArtista(album.getArtista());
            m.setAlbum(album);
            m.setCapaUrl(album.getCapaUrl());
            String data = rec.path("first-release-date").asText("");
            if (data.length() == 10) {
                m.setDtLancamento(LocalDate.parse(data));
            }
            return musicaRepository.save(m);
        });
    }

    private Artista garantirArtista(JsonNode json) {
        JsonNode a = json.path("artist-credit").path(0).path("artist");
        String id = a.path("id").asText("");
        if (id.isBlank()) {
            throw new IllegalArgumentException("Não foi possível identificar o artista.");
        }
        return artistaRepository.findByMbid(id).orElseGet(() -> {
            Artista artista = new Artista();
            artista.setNome(a.path("name").asText());
            artista.setMbid(id);
            return artistaRepository.save(artista);
        });
    }
}