package com.wirebarley.transfer.api.exception;

import com.wirebarley.transfer.core.exception.BusinessException;
import com.wirebarley.transfer.core.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
// 모든 @RestController에 대해 전역적으로 동작
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * [1순위] 우리가 리팩토링한 BusinessException 처리
     * 서비스 로직에서 throw new BusinessException(ErrorCode.XXX)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        // 비즈니스 예외는 WARN 레벨로 로깅합니다. (에러가 아니라 '예상된 예외'이므로)
        log.warn("BusinessException occurred: {} - {}", errorCode.name(), e.getMessage());

        ErrorResponse response = ErrorResponse.of(errorCode);

        // 예: ErrorCode에 HttpStatus 필드를 추가하고 여기서 e.getErrorCode().getStatus()를 사용
        // 여기서는 BAD_REQUEST (400)을 공통으로 사용.
        HttpStatus status = switch (errorCode) {
            case ACCOUNT_NOT_FOUND -> HttpStatus.NOT_FOUND; // 404
            // INSUFFICIENT_FUNDS 등 나머지는 400
            default -> HttpStatus.BAD_REQUEST;
        };

        return new ResponseEntity<>(response, status);
    }

    /**
     * [2순위] @Valid 어노테이션을 통한 DTO 검증 실패 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        // 가장 첫 번째 검증 실패 메시지를 가져옴
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        log.warn("ValidationException occurred: {}", errorMessage);

        // Validation 실패는 공통 코드를 사용 (e.g., "INVALID_INPUT_VALUE")
        ErrorResponse response = ErrorResponse.of("INVALID_INPUT_VALUE", errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * [3순위] 처리되지 않은 모든 예외 (500 Error)
     * 위에서 @ExceptionHandler로 잡지 못한 모든 예외가 여기로 옵니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(Exception e) {
        // 500 에러는 스택 트레이스를 포함하여 ERROR 레벨로 로깅
        log.error("UnhandledException occurred: {}", e.getMessage(), e);

        // 사용자에게는 민감한 스택 트레이스 대신 공통 메시지를 반환
        ErrorResponse response = ErrorResponse.of("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}