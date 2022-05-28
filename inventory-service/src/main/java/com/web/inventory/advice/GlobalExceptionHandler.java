package com.web.inventory.advice;

import com.web.inventory.exception.InternalServerErrorExceptionHandler;
import com.web.inventory.exception.NotFoundException;
import com.web.inventory.exception.RecordNotUpdateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InternalServerErrorExceptionHandler.class)
    public ResponseEntity<?> handleInternalServerErrorException(InternalServerErrorExceptionHandler ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(RecordNotUpdateException.class)
    public ResponseEntity<?> handleRecordNotUpdateException(RecordNotUpdateException ex) {
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(ex.getMessage());
    }

}
