package org.java.user.exception;

import java.util.List;
import org.java.user.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ValidationException.class)
  public ProblemDetail handleValidationException(ValidationException ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        "Validation failed"
    );

    List<ErrorResponse> errors = ex.getValidationErrors().entrySet().stream()
        .map(entry -> new ErrorResponse(entry.getKey(), entry.getValue()))
        .toList();

    problemDetail.setProperty("errors", errors);
    return problemDetail;
  }
}
