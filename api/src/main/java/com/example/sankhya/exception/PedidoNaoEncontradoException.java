package com.example.sankhya.exception;

public class PedidoNaoEncontradoException extends RuntimeException{

    public PedidoNaoEncontradoException(){
        super("Pedido não encontrado");
    }

}
