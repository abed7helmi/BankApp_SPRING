package com.example.ebankingbackend.services;

import com.example.ebankingbackend.dtos.CustomerDTO;
import com.example.ebankingbackend.entities.*;
import com.example.ebankingbackend.enums.OperationType;
import com.example.ebankingbackend.exceptions.BalanceNotSufficentException;
import com.example.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.example.ebankingbackend.exceptions.CustomerNotFoundException;
import com.example.ebankingbackend.mappers.BankAccountMapperImpl;
import com.example.ebankingbackend.repositories.AccountOperationRepository;
import com.example.ebankingbackend.repositories.BankAccountRepository;
import com.example.ebankingbackend.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;
    private final AccountOperationRepository accountOperationRepository;

    private BankAccountMapperImpl bankAccountMapper;

    /*public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, CustomerRepository customerRepository, AccountOperationRepository accountOperationRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.customerRepository = customerRepository;
        this.accountOperationRepository = accountOperationRepository;
    }*/

    @Override
    public Customer saveCustomer(Customer customer) {
        log.info("-------------saveCustomer");
        Customer savedCustomer = customerRepository.save(customer);
        return savedCustomer;
    }

    @Override
    public CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        log.info("-----------saveCurrentBankAccount");
        BankAccount bankAccount;
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        CurrentAccount currentAccount = new CurrentAccount();

        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCreatedAt(new Date());
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraft);

        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);

        return savedBankAccount;
    }

    @Override
    public SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        log.info("-----------saveSavingBankAccount");
        BankAccount bankAccount;
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        SavingAccount savingAccount = new SavingAccount();

        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCreatedAt(new Date());
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);

        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);

        return savedBankAccount;
    }


    @Override
    public BankAccount getBankAccount(String id) throws BankAccountNotFoundException {

        BankAccount bankAccount= bankAccountRepository.findById(id).orElseThrow(() -> new BankAccountNotFoundException("Bank Account Not Found"));
        return bankAccount;


    }

    @Override
    public List<CustomerDTO> listCustomers() {
        List <Customer> customers =  customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream()
                .map(cust -> bankAccountMapper.fromCustomer(cust))
                .collect(Collectors.toList());

        return customerDTOS;
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficentException {
        BankAccount bankAccount = this.getBankAccount(accountId);
        if(bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficentException("Balance not sufficent");
        }

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);

        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = this.getBankAccount(accountId);

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);

        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficentException {
        this.debit(accountIdSource,amount,"Transfer to "+accountIdDestination);
        this.credit(accountIdDestination,amount,"Transfer from "+accountIdSource);

    }

    @Override
    public List<BankAccount> bankAccountList(){
        return bankAccountRepository.findAll();
    }
}
