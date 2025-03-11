package com.PetSitter.config.exception;

public class BadRequestCustom extends RuntimeException { // BadRequest 예외 처리 메시지
    public BadRequestCustom(String message) {
        super(message);
    }
}
