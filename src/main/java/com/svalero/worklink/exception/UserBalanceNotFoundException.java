package com.svalero.worklink.exception;

public class UserBalanceNotFoundException extends RuntimeException {
    public UserBalanceNotFoundException(String message) {
        super(message);
    }
}
