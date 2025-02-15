package com.example.ebankingbackend.exceptions;

public class BalanceNotSufficentException extends Throwable {
    public BalanceNotSufficentException(String message) {
        super(message);
    }
}
