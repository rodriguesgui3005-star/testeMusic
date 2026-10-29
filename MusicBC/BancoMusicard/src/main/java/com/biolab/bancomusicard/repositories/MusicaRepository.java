package com.biolab.bancomusicard.repositories;

import com.biolab.bancomusicard.entities.Musica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MusicaRepository extends JpaRepository<Musica, Integer> {
    Optional<Musica> findByMbid(String mbid);
}
