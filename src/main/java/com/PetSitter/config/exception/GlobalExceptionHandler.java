package com.PetSitter.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage()); // 에러 메시지를 JSON으로 반환

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElementException(NoSuchElementException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    // 전역 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "알 수 없는 오류가 발생했습니다.");
        errorResponse.put("details", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 에러랑 같이 JSON 형식의 오류 메시지 응답
                .body(errorResponse);
    }

    @ExceptionHandler(BadRequestCustom.class)
    public ResponseEntity<Map<String, String>> badRequestException(BadRequestCustom badRequest) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "잘못된 요청입니다.");
        errorResponse.put("details", badRequest.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
