package com.biolab.bancomusicard.controllers;

import com.biolab.bancomusicard.DTOs.CadastroRequest;
import com.biolab.bancomusicard.DTOs.LoginRequest;
import com.biolab.bancomusicard.DTOs.TokenResponse;
import com.biolab.bancomusicard.entities.Usuario;
import com.biolab.bancomusicard.repositories.UsuarioRepository;
import com.biolab.bancomusicard.services.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody CadastroRequest req) {
        if (vazio(req.nome()) || vazio(req.username()) || vazio(req.email()) || vazio(req.senha())) {
            return erro(HttpStatus.BAD_REQUEST, "Preencha nome, username, e-mail e senha.");
        }
        if (req.senha().length() < 8) {
            return erro(HttpStatus.BAD_REQUEST, "A senha deve ter pelo menos 8 caracteres.");
        }
        if (usuarioRepository.existsByEmail(req.email())) {
            return erro(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }
        if (usuarioRepository.existsByUsername(req.username())) {
            return erro(HttpStatus.CONFLICT, "Nome de usuário já em uso.");
        }

        Usuario u = new Usuario();
        u.setNome(req.nome().trim());
        u.setUsername(req.username().trim());
        u.setEmail(req.email().trim().toLowerCase());
        u.setSenha(passwordEncoder.encode(req.senha()));
        usuarioRepository.save(u);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TokenResponse(tokenService.gerar(u), u.getId(), u.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (vazio(req.login()) || vazio(req.senha())) {
            return erro(HttpStatus.BAD_REQUEST, "Informe e-mail ou usuário e a senha.");
        }

        String login = req.login().trim();
        var encontrado = login.contains("@")
                ? usuarioRepository.findByEmail(login.toLowerCase())
                : usuarioRepository.findByUsername(login);

        if (encontrado.isEmpty() || !passwordEncoder.matches(req.senha(), encontrado.get().getSenha())) {
            return erro(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        Usuario u = encontrado.get();
        return ResponseEntity.ok(new TokenResponse(tokenService.gerar(u), u.getId(), u.getUsername()));
    }

    private boolean vazio(String s) {
        return s == null || s.isBlank();
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus status, String msg) {
        return ResponseEntity.status(status).body(Map.of("erro", msg));
    }
}
