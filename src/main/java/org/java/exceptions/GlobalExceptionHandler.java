package org.java.exceptions;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(ReportGenerationException.class)
  public ResponseEntity<ErrorResponse> handleReportGenerationException(
      ReportGenerationException ex) {
    log.error("Report generation error", ex);

    ErrorResponse error = new ErrorResponse(
        "REPORT_GENERATION_ERROR",
        ex.getMessage(),
        Instant.now()
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );

    ErrorResponse error = new ErrorResponse(
        "VALIDATION_ERROR",
        "Invalid request parameters: " + errors.toString(),
        Instant.now()
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error", ex);

    ErrorResponse error = new ErrorResponse(
        "INTERNAL_ERROR",
        "An unexpected error occurred",
        Instant.now()
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  @Data
  @AllArgsConstructor
  public static class ErrorResponse {
    private String code;
    private String message;
    private Instant timestamp;
  }
}
