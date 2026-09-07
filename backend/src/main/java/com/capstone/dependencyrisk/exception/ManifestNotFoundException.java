package com.capstone.dependencyrisk.exception;

public class ManifestNotFoundException extends RuntimeException {
    public ManifestNotFoundException(String message) {
        super(message);
    }
}
