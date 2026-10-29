package com.biolab.bancomusicard.DTOs;

public record AvaliacaoResponse
        (Integer id, String tipo, String mbid, String titulo,
                                String artista, Float nota, String resenha) {

}