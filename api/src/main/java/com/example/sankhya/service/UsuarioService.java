package com.example.sankhya.service;

import com.example.sankhya.domain.Usuario;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    public Usuario autenticar(String username, String password) {
        Optional<Usuario> usuario = Usuario.usuarios.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst();
        return usuario.orElse(null);
    }
}
