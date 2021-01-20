package com.pgbezerra.bezerras.resources.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError {

    private static final long serialVersionUID = -6972984073697391895L;

    private final List<FieldMessage> erros = new ArrayList<>();

    public ValidationError(LocalDateTime time, Integer status, String error, String message, String path) {
        super(time, status, error, message, path);
    }

    public List<FieldMessage> getErros() {
        return erros;
    }

    public void addError(String fieldName, String message){
        erros.add(new FieldMessage(fieldName, message));
    }
}
