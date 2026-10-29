package com.biolab.bancomusicard.services;

import com.biolab.bancomusicard.DTOs.AlbumResumoDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;
import org.springframework.http.HttpHeaders;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class MusicBrainzService {

    private static final String BASE_URL = "https://musicbrainz.org/ws/2/";
    private static final String CAPA_URL = "https://coverartarchive.org/release-group/%s/front-250";
    private static final String UA = "MusicardTCC/1.0.0 ( contato@musicard.com )";
    private static final Pattern MBID = Pattern.compile("^[0-9a-fA-F-]{36}$");
    private long ultimaChamada = 0;

    public JsonNode detalharAlbum(String mbid) {
        return buscar("release-group/" + validar(mbid), "artists");
    }

    public JsonNode detalharMusica(String mbid) {
        return buscar("recording/" + validar(mbid), "artists+releases+release-groups");
    }

    private String validar(String mbid) {
        if (mbid == null || !MBID.matcher(mbid).matches()) {
            throw new IllegalArgumentException("MBID inválido.");
        }
        return mbid;
    }

    private JsonNode buscar(String caminho, String inc) {
        URI uri = UriComponentsBuilder.fromUriString(BASE_URL + caminho)
                .queryParam("inc", inc)
                .queryParam("fmt", "json")
                .build().toUri();
        return get(uri);
    }

    // o MusicBrainz limita a ~1 requisição por segundo
    private synchronized JsonNode get(URI uri) {
        long espera = 1100 - (System.currentTimeMillis() - ultimaChamada);
        if (espera > 0) {
            try { Thread.sleep(espera); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", UA);
        try {
            return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class).getBody();
        } finally {
            ultimaChamada = System.currentTimeMillis();
        }
    }
    private final RestTemplate restTemplate = new RestTemplate();

    public List<AlbumResumoDTO> buscarAlbuns(String termo) {
        URI uri = UriComponentsBuilder.fromUriString(BASE_URL + "release-group")
                .queryParam("query", "{q}")
                .queryParam("fmt", "json")
                .queryParam("limit", 20)
                .encode()
                .buildAndExpand(termo)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "MusicardTCC/1.0.0 ( contato@musicard.com )");

        JsonNode raiz = restTemplate
                .exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class)
                .getBody();

        List<AlbumResumoDTO> resultado = new ArrayList<>();
        if (raiz == null) return resultado;

        for (JsonNode rg : raiz.path("release-groups")) {
            String mbid = rg.path("id").asText();
            resultado.add(new AlbumResumoDTO(
                    mbid,
                    rg.path("title").asText(),
                    rg.path("artist-credit").path(0).path("name").asText(""),
                    rg.path("first-release-date").asText(""),
                    rg.path("primary-type").asText(""),
                    CAPA_URL.formatted(mbid)
            ));
        }
        return resultado;

    }
}