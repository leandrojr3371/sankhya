package com.example.sankhya.controller;

import com.example.sankhya.domain.Cliente;
import com.example.sankhya.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @RequestMapping(value = "/hello/{name}")
    public String helloClient(@PathVariable("name") String nameClient){
        return String.format("Hello %s", nameClient);
    }

    @GetMapping("{id}")
    public Cliente getClientById(@PathVariable Long id){
        return clienteRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Cliente não encontrado"));
    }

    @PostMapping("/salvar")
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente save(@RequestBody @Valid Cliente client){
        return clienteRepository.save(client);
    }

    @DeleteMapping("/remove/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        clienteRepository.findById(id)
                .map(client -> {
                    clienteRepository.delete(client);
                    return client;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Cliente não encontrado"));

    }

    @PutMapping("/atualiza/{id}")
    public void update(@PathVariable Long id, @RequestBody Cliente cliente){
        clienteRepository.findById(id).map((clientExists->{
            cliente.setId(clientExists.getId());
            clienteRepository.save(cliente);
            return clientExists;
        })).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Cliente não encontrado"));
    }

    @GetMapping("/filter")
    public Set<Cliente> filtrarClientes(Cliente filtro){
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Cliente> example = Example.of(filtro, matcher);
        List<Cliente> requestsList = clienteRepository.findAll(example);

        return new HashSet<>(requestsList);

    }

    @GetMapping("/listar")
    public Set<Cliente> find(){
        return new HashSet<>(clienteRepository.findAll());
    }

}
