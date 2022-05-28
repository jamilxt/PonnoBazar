package com.web.product.exception;

public class InternalServerErrorException extends RuntimeException{
    private String message;

    public InternalServerErrorException(String message) {
        super(message);
    }
}
