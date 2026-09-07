package com.capstone.dependencyrisk.exception;

public class RepositoryAccessDeniedException extends RuntimeException {
    public RepositoryAccessDeniedException(String message) {
        super(message);
    }
}
