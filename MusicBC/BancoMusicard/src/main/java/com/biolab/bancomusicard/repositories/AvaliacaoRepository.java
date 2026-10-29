package com.biolab.bancomusicard.repositories;

import com.biolab.bancomusicard.entities.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Integer> {
    Optional<Avaliacao> findByUsuarioIdAndMusicaId(Integer usuarioId, Integer musicaId);
    Optional<Avaliacao> findByUsuarioIdAndAlbumId(Integer usuarioId, Integer albumId);
}
