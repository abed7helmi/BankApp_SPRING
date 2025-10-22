package com.example.ebankingbackend.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class BankAccountNotFoundException extends EntityNotFoundException {
    public BankAccountNotFoundException(String message) {
        super(message);
    }
}
