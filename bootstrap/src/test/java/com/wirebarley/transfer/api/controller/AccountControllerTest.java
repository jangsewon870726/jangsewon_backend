package com.wirebarley.transfer.api.controller;

import com.wirebarley.transfer.core.dto.AccountDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * AccountController 통합 테스트
 * Testcontainers를 사용하여 실제 PostgreSQL DB로 테스트
 * 테스트 순서가 중요하므로 @TestMethodOrder 사용
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountControllerTest {

    @LocalServerPort
    private int port;

    private static String userAAccountNumber;
    private static String userBAccountNumber;
    private static final String userAPassword = "1111";
    private static final String userBPassword = "2222";

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @Order(1)
    @DisplayName("신규 계좌 A를 생성한다")
    void createAccountA() {
        AccountDto.CreateRequest request = new AccountDto.CreateRequest(
                "User A", userAPassword, new BigDecimal("100000") // 10만원
        );

        userAAccountNumber = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/accounts")
                .then()
                .statusCode(HttpStatus.CREATED.value()) // 201
                .body("accountId", notNullValue())
                .body("accountNumber", startsWith("110-")) // "110-"으로 시작하는지
                .body("ownerName", equalTo("User A"))
                .body("balance", equalTo(100000)) // 정수로 비교
                .extract().path("accountNumber"); // 생성된 계좌번호 추출

        System.out.println("User A 계좌번호: " + userAAccountNumber);
    }

    @Test
    @Order(2)
    @DisplayName("신규 계좌 B를 생성한다")
    void createAccountB() {
        AccountDto.CreateRequest request = new AccountDto.CreateRequest(
                "User B", userBPassword, new BigDecimal("50000") // 5만원
        );

        userBAccountNumber = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/accounts")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("accountNumber", startsWith("110-"))
                .extract().path("accountNumber");

        System.out.println("User B 계좌번호: " + userBAccountNumber);
    }

    @Test
    @Order(3)
    @DisplayName("계좌 A에서 계좌 B로 30000원을 이체한다 (성공)")
    void transfer_success() {
        AccountDto.TransferRequest request = new AccountDto.TransferRequest(
                userAAccountNumber, userAPassword, userBAccountNumber, new BigDecimal("30000")
        );

        // 이체 전 잔액 확인
        checkBalance(userAAccountNumber, 100000); // A: 10만원
        checkBalance(userBAccountNumber, 50000);  // B: 5만원

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("fromAccountNumber", equalTo(userAAccountNumber))
                .body("toAccountNumber", equalTo(userBAccountNumber))
                .body("transferredAmount", equalTo(30000));

        // 이체 후 잔액 확인 (수수료 1% = 300원)
        // A: 100000 - 30000 - 300 = 69700
        // B: 50000 + 30000 = 80000
        checkBalance(userAAccountNumber, 69700);
        checkBalance(userBAccountNumber, 80000);
    }

    @Test
    @Order(4)
    @DisplayName("계좌 A에서 10000원을 출금한다 (성공)")
    void withdraw_success() {
        AccountDto.WithdrawRequest request = new AccountDto.WithdrawRequest(
                userAAccountNumber, userAPassword, new BigDecimal("10000")
        );

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/accounts/withdraw")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("accountNumber", equalTo(userAAccountNumber))
                .body("finalBalance", equalTo(59700)); // 69700 - 10000 = 59700
    }

    @Test
    @Order(5)
    @DisplayName("계좌 A의 거래 내역을 조회한다 (총 3건)")
    void getTransactionHistory() {
        RestAssured.given()
                .pathParam("accountNumber", userAAccountNumber)
                .when()
                .get("/api/v1/accounts/{accountNumber}/history")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("accountNumber", equalTo(userAAccountNumber))
                .body("transactions", hasSize(3)) // 1.생성(입금), 2.송금, 3.출금
                .body("transactions[0].type", equalTo("WITHDRAW")) // 최신순
                .body("transactions[1].type", equalTo("TRANSFER_SEND"))
                .body("transactions[2].type", equalTo("DEPOSIT"));
    }

    @Test
    @Order(6)
    @DisplayName("계좌 A 삭제 시 잔액이 남아있어 실패한다 (400 Bad Request)")
    void deleteAccount_fail_balance_not_zero() {
        AccountDto.DeleteRequest request = new AccountDto.DeleteRequest(userAPassword);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("accountNumber", userAAccountNumber)
                .body(request)
                .when()
                .delete("/api/v1/accounts/{accountNumber}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value()) // 400
                .body("code", equalTo("BALANCE_NOT_ZERO"));
    }

    private void checkBalance(String accountNumber, int expectedBalance) {
        RestAssured.given()
                .pathParam("accountNumber", accountNumber)
                .when()
                .get("/api/v1/accounts/{accountNumber}/balance")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("balance", equalTo(expectedBalance));
    }
}