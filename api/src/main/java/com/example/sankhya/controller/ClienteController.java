package com.example.sankhya.controller;

import com.example.sankhya.domain.Cliente;
import com.example.sankhya.repository.ClienteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cliente", description = "Operações relacionadas aos clientes.")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Operation(summary = "Buscar Cliente por ID", description = "Retorna os detalhes de um cliente baseado no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            })
    @GetMapping("{id}")
    public Cliente getClientById(@PathVariable Long id){
        return clienteRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Cliente não encontrado"));
    }

    @Operation(summary = "Salvar um novo Cliente", description = "Cria um novo cliente na base de dados.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso")
            })
    @PostMapping("/salvar")
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente save(@RequestBody @Valid Cliente client){
        return clienteRepository.save(client);
    }

    @Operation(summary = "Excluir Cliente", description = "Remove um cliente da base de dados baseado no ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            })
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

    @Operation(summary = "Atualizar Cliente", description = "Atualiza os dados de um cliente existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
            })
    @PutMapping("/atualiza/{id}")
    public void update(@PathVariable Long id, @RequestBody Cliente cliente){
        clienteRepository.findById(id).map((clientExists->{
            cliente.setId(clientExists.getId());
            clienteRepository.save(cliente);
            return clientExists;
        })).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Cliente não encontrado"));
    }

    @Operation(summary = "Filtrar Clientes", description = "Filtra os clientes com base no objeto cliente fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de clientes filtrados"),
                    @ApiResponse(responseCode = "404", description = "Nenhum cliente encontrado")
            })
    @GetMapping("/filter")
    public Set<Cliente> filtrarClientes(Cliente filtro){
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Cliente> example = Example.of(filtro, matcher);
        List<Cliente> requestsList = clienteRepository.findAll(example);

        return new HashSet<>(requestsList);

    }

    @Operation(summary = "Listar Todos os Clientes", description = "Retorna todos os clientes cadastrados na base de dados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de todos os clientes")
            })
    @GetMapping("/listar")
    public Set<Cliente> find(){
        return new HashSet<>(clienteRepository.findAll());
    }

}
