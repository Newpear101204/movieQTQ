package com.movie.movie.exception;

public class AccountExist extends RuntimeException {
    public AccountExist(String message) {
        super(message);
    }
}
