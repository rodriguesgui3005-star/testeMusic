package com.biolab.bancomusicard.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "albuns")
@Getter
@Setter
@NoArgsConstructor

public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_albuns")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_artistas", nullable = false)
    private Artista artista;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(name = "duracao_segundos", nullable = false)
    private Integer duracaoSegundos;

    @Column(length = 36)
    private String mbid;

    @Column(name = "capa_url", length = 500)
    private String capaUrl;
}
