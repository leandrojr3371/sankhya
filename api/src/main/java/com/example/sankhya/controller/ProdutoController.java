package com.example.sankhya.controller;

import com.example.sankhya.domain.Produto;
import com.example.sankhya.repository.ProdutoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Operation(summary = "Salvar um novo Produto", description = "Cria um novo produto na base de dados.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso")
            })
    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public Produto save(@RequestBody Produto produto){
        return produtoRepository.save(produto);
    }

    @Operation(summary = "Atualizar Produto", description = "Atualiza os dados de um produto existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
            })
    @PutMapping("/atualiza/{id}")
    public void update(@RequestBody Produto produto, @PathVariable Long id){
        produtoRepository.findById(id).map(p->{
            produto.setId(id);
            produtoRepository.save(produto);
            return produto;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Produto não encontrado"));
    }

    @Operation(summary = "Excluir Produto", description = "Remove um produto da base de dados baseado no ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
            })
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        produtoRepository.findById(id)
                .map(product -> {
                    produtoRepository.delete(product);
                    return Void.TYPE;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Produto não encontrado"));
    }

    @Operation(summary = "Buscar Produto por ID", description = "Retorna os detalhes de um produto baseado no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Produto encontrado"),
                    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
            })
    @GetMapping("{id}")
    public Produto getById(@PathVariable Long id){
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Produto não encontrado"));
    }

    @Operation(summary = "Filtrar Produtos", description = "Filtra os produtos com base no objeto produto fornecido. As propriedades não fornecidas serão ignoradas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de produtos filtrados"),
                    @ApiResponse(responseCode = "404", description = "Nenhum produto encontrado")
            })
    @GetMapping("/filter")
    public Set<Produto> find(Produto filter){
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Produto> example = Example.of(filter, matcher);
        List<Produto> requestsList = produtoRepository.findAll(example);

        return new HashSet<>(requestsList);
    }

    @Operation(summary = "Listar Todos os Produtos", description = "Retorna todos os produtos cadastrados na base de dados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de todos os produtos")
            })
    @GetMapping("/listar")
    public Set<Produto> find(){
        return new HashSet<>(produtoRepository.findAll());
    }
}
