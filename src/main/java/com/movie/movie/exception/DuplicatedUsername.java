package com.movie.movie.exception;

public class DuplicatedUsername extends RuntimeException {
    public DuplicatedUsername(String message) {
        super(message);
    }
}
