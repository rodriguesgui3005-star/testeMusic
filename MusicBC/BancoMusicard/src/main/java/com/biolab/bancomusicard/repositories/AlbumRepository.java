package com.biolab.bancomusicard.repositories;

import com.biolab.bancomusicard.entities.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Integer> {
    Optional<Album> findByMbid(String mbid);
}
