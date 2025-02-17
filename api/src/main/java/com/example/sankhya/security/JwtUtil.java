package com.example.sankhya.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
    private static final String CHAVE_SECRETA = "minhaChaveSecretaSuperSeguraComMaisDe32Caracteres";
    private static final long TEMPO_EXPIRACAO = 1000 * 60 * 60; // 1 hora
    private final SecretKey chave;

    @Autowired
    public JwtUtil() {
        this.chave = Keys.hmacShaKeyFor(CHAVE_SECRETA.getBytes());
    }

    // Gerar o token
    public String gerarToken(String username, List<String> roles) {
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TEMPO_EXPIRACAO))
                .signWith(chave)
                .compact();
    }

    public String extrairUsuario(String token) {
        return extrairClaims(token).getSubject();
    }

    public List<String> extrairRoles(String token) {
        return extrairClaims(token).get("roles", List.class);
    }

    public boolean validarToken(String token) {
        return !extrairExpiracao(token).before(new Date());
    }

    private Date extrairExpiracao(String token) {
        return extrairClaims(token).getExpiration();
    }

    public Claims extrairClaims(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(chave) // Usa a chave diretamente (sem cast)
                .build();
        return parser.parseSignedClaims(token).getPayload(); // Retorna o payload do token
    }
}
