package com.biolab.bancomusicard.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "artistas")
@Getter
@Setter
@NoArgsConstructor
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_artistas")
    private Integer id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @Column(nullable = false, length = 36)
    private String mbid;

    @Column(name = "pais_origem", length = 100)
    private String paisOrigem;
}