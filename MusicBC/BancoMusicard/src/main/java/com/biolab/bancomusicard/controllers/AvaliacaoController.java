package com.biolab.bancomusicard.controllers;

import com.biolab.bancomusicard.DTOs.AvaliacaoRequest;
import com.biolab.bancomusicard.services.AvaliacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> avaliar(@AuthenticationPrincipal Jwt jwt,
                                     @RequestBody AvaliacaoRequest req) {
        try {
            Integer usuarioId = Integer.valueOf(jwt.getSubject());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.avaliar(usuarioId, req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "MBID não encontrado no MusicBrainz."));
        }
    }
}
