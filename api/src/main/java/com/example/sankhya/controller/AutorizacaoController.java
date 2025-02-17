package com.example.sankhya.controller;

import com.example.sankhya.domain.Usuario;
import com.example.sankhya.security.JwtUtil;
import com.example.sankhya.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AutorizacaoController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {
        Usuario usuario = usuarioService.autenticar(username, password);
        if (usuario != null) {
            String token = jwtUtil.gerarToken(username, usuario.getRoles());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            throw new RuntimeException("Usuário ou senha inválidos");
        }
    }

}
