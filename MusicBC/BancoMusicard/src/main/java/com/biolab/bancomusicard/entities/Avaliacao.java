package com.biolab.bancomusicard.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "avaliacoes")
@Getter
@Setter
@NoArgsConstructor
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacoes")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_usuario", nullable = false)
    private Usuario usuario;

    @Column(length = 1000)
    private String resenha;

    private Float nota;   // Float, não Double: o MySQL FLOAT só passa no validate assim

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_musicas")
    private Musica musica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_albuns")
    private Album album;
}
