package com.example.ebankingbackend.services;

import com.example.ebankingbackend.dtos.CustomerDTO;
import com.example.ebankingbackend.entities.BankAccount;
import com.example.ebankingbackend.entities.CurrentAccount;
import com.example.ebankingbackend.entities.Customer;
import com.example.ebankingbackend.entities.SavingAccount;
import com.example.ebankingbackend.exceptions.BalanceNotSufficentException;
import com.example.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.example.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customer) ;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    BankAccount getBankAccount(String id) throws BankAccountNotFoundException;
    List<CustomerDTO> listCustomers();
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficentException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficentException;

    List<BankAccount> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;
}
