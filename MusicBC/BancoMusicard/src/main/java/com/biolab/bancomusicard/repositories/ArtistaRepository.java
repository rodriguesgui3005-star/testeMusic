package com.biolab.bancomusicard.repositories;

import com.biolab.bancomusicard.entities.Artista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistaRepository extends JpaRepository<Artista, Integer> {
    Optional<Artista> findByMbid(String mbid);
}
