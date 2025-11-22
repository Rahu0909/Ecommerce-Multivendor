package com.project.ecommerce.exceptions;

import com.project.ecommerce.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> response = new HashMap<>();
        e.getBindingResult()
                .getAllErrors()
                .forEach(err -> {
                    String fieldName = ((FieldError) err).getField();
                    String message = err.getDefaultMessage();
                    response.put(fieldName,
                            message);
                });
        return new ResponseEntity<Map<String, String>>(response,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e) {
        String message = e.getMessage();
        APIResponse response = new APIResponse(message,
                false);
        return new ResponseEntity<>(response,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myAPIException(APIException e) {
        String message = e.getMessage();
        APIResponse response = new APIResponse(message,
                false);
        return new ResponseEntity<>(response,
                HttpStatus.BAD_REQUEST);
    }
}
