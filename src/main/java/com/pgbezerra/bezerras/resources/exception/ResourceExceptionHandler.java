package com.pgbezerra.bezerras.resources.exception;

import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import com.pgbezerra.bezerras.services.exception.ResourceBadRequestException;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ResourceExceptionHandler {

    private static final Logger LOG = Logger.getLogger(ResourceExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> notFound(ResourceNotFoundException ex, HttpServletRequest request) {
        String error = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        LOG.info(ex.getMessage());
        StandardError standardError = new StandardError(LocalDateTime.now(), status.value(), error, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(ResourceBadRequestException.class)
    public ResponseEntity<StandardError> badRequest(ResourceBadRequestException ex, HttpServletRequest request) {
        String error = "Bad request";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        LOG.info(ex.getMessage());
        StandardError standardError = new StandardError(LocalDateTime.now(), status.value(), error, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> database(DatabaseException ex, HttpServletRequest request) {
        String error = "Database error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        LOG.info(ex.getMessage());
        StandardError standardError = new StandardError(LocalDateTime.now(), status.value(), error, ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(standardError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> badRequest(MethodArgumentNotValidException e, HttpServletRequest request) {
        String error = "Bad request";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ValidationError validationError = new ValidationError(LocalDateTime.now(), status.value(), error, e.getMessage(), request.getRequestURI());

        for (FieldError x : e.getBindingResult().getFieldErrors())
            validationError.addError(x.getField(), x.getDefaultMessage());


        return ResponseEntity.status(status).body(validationError);
    }


}
