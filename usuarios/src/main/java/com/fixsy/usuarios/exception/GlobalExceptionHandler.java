package com.fixsy.usuarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> error = new HashMap<>();
        String message = e.getMessage();
        
        // Manejar diferentes tipos de errores
        if (message != null && message.contains("ya está registrado")) {
            error.put("error", "El email ya está registrado");
            error.put("message", message);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } else if (message != null && message.contains("no encontrado")) {
            error.put("error", "Recurso no encontrado");
            error.put("message", message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } else {
            error.put("error", "Error del servidor");
            error.put("message", message != null ? message : "Error desconocido");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            // Ignorar errores de validación del campo password si es null o vacío (es opcional en actualizaciones)
            // El password solo es requerido al crear usuario, no al actualizar
            if (!"password".equals(fieldName)) {
                fieldErrors.put(fieldName, errorMessage);
            }
        });
        
        errors.put("error", "Error de validación");
        errors.put("message", "Los datos proporcionados no son válidos");
        if (!fieldErrors.isEmpty()) {
            errors.put("fieldErrors", fieldErrors);
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Error interno del servidor");
        error.put("message", e.getMessage() != null ? e.getMessage() : "Error desconocido");
        // Log del error completo para debug
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

