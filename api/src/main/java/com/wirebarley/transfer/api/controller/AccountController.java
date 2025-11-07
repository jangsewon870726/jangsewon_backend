package com.wirebarley.transfer.api.controller;

import com.wirebarley.transfer.core.dto.AccountDto;
import com.wirebarley.transfer.core.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "계좌 (Account)", description = "계좌 CRUD, 잔액 조회, 입출금/송금 및 거래내역 조회 API")
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * 계좌 생성 API
     * [POST] /api/v1/accounts
     */
    @Operation(summary = "신규 계좌 생성", description = "소유주명, 비밀번호, 초기 입금액으로 새로운 계좌를 생성합니다. (계좌번호 자동 발급)")
    @PostMapping
    public ResponseEntity<AccountDto.CreateResponse> createAccount(
            @Valid @RequestBody AccountDto.CreateRequest request
    ) {
        AccountDto.CreateResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 계좌 삭제 API
     * [DELETE] /api/v1/accounts/{accountNumber}
     */
    @Operation(summary = "계좌 삭제", description = "계좌 번호와 비밀번호로 특정 계좌를 삭제합니다. (단, 잔액이 0원이어야 함)")
    @Parameter(name = "accountNumber", description = "삭제할 계좌 번호", example = "110-123-456789")
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable("accountNumber") String accountNumber,
            @Valid @RequestBody AccountDto.DeleteRequest request
    ) {
        accountService.deleteAccount(accountNumber, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 계좌 잔액 조회 API
     * [GET] /api/v1/accounts/{accountNumber}/balance
     */
    @Operation(summary = "계좌 잔액 조회", description = "계좌 번호로 계좌 소유주명과 잔액을 조회합니다.")
    @Parameter(name = "accountNumber", description = "조회할 계좌 번호", example = "110-123-456789")
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<AccountDto.BalanceResponse> getAccountBalance(
            @PathVariable("accountNumber") String accountNumber
    ) {
        AccountDto.BalanceResponse response = accountService.getAccountBalance(accountNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * 송금 API
     * [POST] /api/v1/accounts/transfer
     */
    @Operation(summary = "송금 실행", description = "지정된 계좌에서 다른 계좌로 금액을 이체합니다. (비밀번호 검증, 수수료 1%, 1일 300만원 한도)")
    @PostMapping("/transfer")
    public ResponseEntity<AccountDto.TransferResponse> transfer(
            @Valid @RequestBody AccountDto.TransferRequest request
    ) {
        AccountDto.TransferResponse response = accountService.transfer(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 거래 내역 조회 API
     * [GET] /api/v1/accounts/{accountNumber}/history
     */
    @Operation(summary = "거래 내역 조회", description = "계좌 번호로 해당 계좌의 모든 송금/수취 내역을 최신순으로 조회합니다.")
    @Parameter(name = "accountNumber", description = "조회할 계좌 번호", example = "110-123-456789")
    @GetMapping("/{accountNumber}/history")
    public ResponseEntity<AccountDto.HistoryResponse> getTransactionHistory(
            @PathVariable("accountNumber") String accountNumber
    ) {
        AccountDto.HistoryResponse response = accountService.getTransactionHistory(accountNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * 입금 API
     * [POST] /api/v1/accounts/deposit
     */
    @Operation(summary = "입금 실행", description = "지정된 계좌에 금액을 입금합니다.")
    @PostMapping("/deposit")
    public ResponseEntity<AccountDto.DepositResponse> deposit(
            @Valid @RequestBody AccountDto.DepositRequest request
    ) {
        AccountDto.DepositResponse response = accountService.deposit(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 출금 API
     * [POST] /api/v1/accounts/withdraw
     */
    @Operation(summary = "출금 실행", description = "지정된 계좌에서 금액을 출금합니다. (비밀번호 검증, 1일 100만원 한도)")
    @PostMapping("/withdraw")
    public ResponseEntity<AccountDto.WithdrawResponse> withdraw(
            @Valid @RequestBody AccountDto.WithdrawRequest request
    ) {
        AccountDto.WithdrawResponse response = accountService.withdraw(request);
        return ResponseEntity.ok(response);
    }
}