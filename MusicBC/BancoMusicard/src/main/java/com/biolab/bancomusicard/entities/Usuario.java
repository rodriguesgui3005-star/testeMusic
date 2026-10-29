package com.biolab.bancomusicard.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuarios")
    private Integer id;

    @Column(nullable = false, length = 30)
    private String nome;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(nullable = false, length = 200)
    private String senha;

    @Column(length = 500)
    private String biografia;

    @Column(name = "foto_perfil_url", length = 200)
    private String fotoPerfilUrl;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro = LocalDate.now();
}
