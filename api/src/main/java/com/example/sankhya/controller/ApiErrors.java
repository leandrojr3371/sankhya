package com.example.sankhya.controller;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class ApiErrors {

    private List<String> errors;

    public ApiErrors(String messageError){
        this.errors = Arrays.asList(messageError);
    }

    public ApiErrors(List<String> messageErrors){
        this.errors = messageErrors;
    }
}
