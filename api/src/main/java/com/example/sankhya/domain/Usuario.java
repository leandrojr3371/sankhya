package com.example.sankhya.domain;

import java.util.Arrays;
import java.util.List;

public class Usuario {
    private String username;
    private String password;
    private List<String> roles;

    public Usuario(String username, String password, List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public static List<Usuario> usuarios = Arrays.asList(
            new Usuario("funcionario", "senha123", List.of("FUNCIONARIO")),
            new Usuario("chefe", "senha123", List.of("CHEFE"))
    );
}
