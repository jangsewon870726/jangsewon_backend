# 계좌 이체 서비스 (Account Transfer Service)

Spring Boot와 헥사고날 아키텍처 기반의 계좌 이체 및 관리 API 서비스입니다.

## 실행 방법

**사전 준비:** **Docker Desktop** 실행

### 1\. 전체 실행 (권장: App + DB)

프로젝트 루트에서 다음 명령어를 실행하면 `Dockerfile` 빌드 및 `app`, `db` 컨테이너가 함께 실행됩니다.

```shell
docker-compose up --build
```

### 2\. 개발용 실행 (IDE + DB)

1.  터미널에서 DB 컨테이너만 실행합니다.
    ```shell
    docker-compose up -d db
    ```
2.  IDE에서 `bootstrap` 모듈의 `TransferServiceApplication.java`를 직접 실행(Run/Debug)합니다.

-----

## API 명세

애플리케이션 실행 후, 아래 주소에서 API 명세를 확인하고 테스트할 수 있습니다.

* **Swagger UI:** `http://localhost:8080/swagger-ui.html`

### 주요 API

* `POST /api/v1/accounts`: 계좌 생성 (번호 자동 발급)
* `DELETE /api/v1/accounts/{accountNumber}`: 계좌 삭제 (Soft Delete)
* `GET /api/v1/accounts/{accountNumber}/balance`: 잔액 조회
* `POST /api/v1/accounts/deposit`: 입금
* `POST /api/v1/accounts/withdraw`: 출금
* `POST /api/v1/accounts/transfer`: 이체 (송금)
* `GET /api/v1/accounts/{accountNumber}/history`: 거래 내역 조회

-----

## 주요 스펙 및 정책

* **계좌번호:** `110-` 접두사로 자동 생성 및 중복 검증
* **비밀번호:** `SHA-256` + `Salt`로 암호화
* **금액:** 모든 금액은 소수점 없는 정수(원)로 처리
* **삭제:** `Soft Delete` (플래그) 방식 적용. (잔액 0원 및 비밀번호 검증 필요)
* **수수료 (이체):** 이체 금액의 1%, 소수점 이하 **버림**
* **한도 (24시간):** 이체 (3,000,000원), 출금 (1,000,000원)
* **동시성:** `비관적 락(Pessimistic Lock)`을 사용해 데이터 정합성 보장 (데드락 방지 로직 포함)
* **거래내역:** 계좌 생성(초기 입금)을 포함한 모든 입출금/이체 내역 기록

-----

## 아키텍처

**헥사고날 아키텍처 (DIP)** 기반의 4개 모듈로 분리되었습니다.

* `core`: 순수 도메인, DTO, 인터페이스(Port) (Spring 의존성 없음)
* `api`: 웹 어댑터 (`Controller`), 서비스 구현체 (`ServiceImpl`) (Spring 의존)
* `infra`: DB 어댑터 (`RepositoryImpl`, `Entity`) (JPA 의존)
* `bootstrap`: 모든 모듈 조립 및 실행 (`@SpringBootApplication`)

-----

## 기술 스택

* **Server:** Java 17, Spring Boot 3.3, Spring Data JPA / Hibernate
* **Database:** PostgreSQL (Docker)
* **Build:** Gradle (Multi-module)
* **API Docs:** Springdoc (Swagger 3)
* **Test:** JUnit 5, Testcontainers, RestAssured

-----

## 테스트

* **단위 테스트:** `core` 모듈 (`AccountTest`)
* **통합 테스트:** `bootstrap` 모듈 (`AccountControllerTest`)
    * **Testcontainers**가 테스트용 DB를 자동으로 실행합니다. (Docker Desktop 실행 필수)

### 전체 테스트 실행

```shell
gradlew clean test
```