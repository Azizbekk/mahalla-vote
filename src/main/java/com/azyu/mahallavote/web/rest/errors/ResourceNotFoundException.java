package com.azyu.mahallavote.web.rest.errors;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String entityName, Long id) {
        super(entityName + " not found with id: " + id);
    }
}
