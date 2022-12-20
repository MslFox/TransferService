package com.example.transfer.exception;

import com.example.transfer.model.ErrorMessage;
import com.example.transfer.model.RejectedTransferBody;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Log
@RestControllerAdvice
public class ApplicationExceptionHandlers {
    @ExceptionHandler
    public ResponseEntity<Object> exceptionHandle(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(RejectedTransferBody.builder().
                message(Objects.requireNonNull(e.getFieldError()).getDefaultMessage()).build());
    }

    @ExceptionHandler
    public ResponseEntity<Object> exceptionHandle(TransactionException e) {
        return ResponseEntity.internalServerError().body(RejectedTransferBody.builder().
                message(e.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exceptionHandle() {
        return ResponseEntity.internalServerError().body(RejectedTransferBody.builder().
                message(ErrorMessage.SERVER_ERROR).build());
    }
}


