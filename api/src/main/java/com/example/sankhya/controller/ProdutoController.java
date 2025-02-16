package com.example.sankhya.controller;

import com.example.sankhya.domain.Produto;
import com.example.sankhya.repository.ProdutoRepository;
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

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public Produto save(@RequestBody Produto produto){
        return produtoRepository.save(produto);
    }

    @PutMapping("/atualiza/{id}")
    public void update(@RequestBody Produto produto, @PathVariable Long id){
        produtoRepository.findById(id).map(p->{
            produto.setId(id);
            produtoRepository.save(produto);
            return produto;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Produto não encontrado"));
    }

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

    @GetMapping("{id}")
    public Produto getById(@PathVariable Long id){
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Produto não encontrado"));
    }

    @GetMapping("/filter")
    public Set<Produto> find(Produto filter){
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Produto> example = Example.of(filter, matcher);
        List<Produto> requestsList = produtoRepository.findAll(example);

        return new HashSet<>(requestsList);
    }

    @GetMapping("/listar")
    public Set<Produto> find(){
        return new HashSet<>(produtoRepository.findAll());
    }
}
