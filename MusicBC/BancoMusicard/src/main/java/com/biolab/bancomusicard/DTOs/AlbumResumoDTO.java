package com.biolab.bancomusicard.DTOs;

public record AlbumResumoDTO(
        String mbid,
        String titulo,
        String artista,
        String dataLancamento,
        String tipo,
        String capaUrl
) {}
