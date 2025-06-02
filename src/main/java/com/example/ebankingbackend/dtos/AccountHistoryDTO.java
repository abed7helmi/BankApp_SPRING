package com.example.ebankingbackend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class AccountHistoryDTO {
    private String accountId;
    private double balance;
    private int totalPages;
    private int pageSize;
    private int CurrentPage;
    private String type;
    private List<AccountOperationDTO> accountOperationDTOS;
}
