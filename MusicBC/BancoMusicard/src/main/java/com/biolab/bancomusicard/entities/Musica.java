package com.biolab.bancomusicard.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "musicas")
@Getter
@Setter
@NoArgsConstructor
public class Musica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_musicas")
    private Integer id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(name = "dt_lancamento")
    private java.time.LocalDate dtLancamento;

    @Column(nullable = false, length = 36)
    private String mbid;

    @Column(name = "capa_url", length = 300)
    private String capaUrl;

    @Column(name = "numero_faixa")
    private Integer numeroFaixa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_artista", nullable = false)
    private Artista artista;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_albuns", nullable = false)
    private Album album;
}
