package com.wirebarley.transfer.core.service;

import com.wirebarley.transfer.core.dto.AccountDto;

public interface AccountService {

    AccountDto.CreateResponse createAccount(AccountDto.CreateRequest request);

    void deleteAccount(String accountNumber, AccountDto.DeleteRequest request);

    AccountDto.BalanceResponse getAccountBalance(String accountNumber);

    AccountDto.TransferResponse transfer(AccountDto.TransferRequest request);

    AccountDto.HistoryResponse getTransactionHistory(String accountNumber);

    AccountDto.DepositResponse deposit(AccountDto.DepositRequest request);

    AccountDto.WithdrawResponse withdraw(AccountDto.WithdrawRequest request);
}