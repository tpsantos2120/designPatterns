package org.java.exception;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for the application.
 * Uses Spring's @ControllerAdvice to handle exceptions across all controllers.
 * Returns RFC 7807 Problem Detail responses for standardized error handling.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handle missing request body.
   * Returns a 400 Bad Request with Problem Detail.
   *
   * @param ex The exception that was thrown
   * @param request The web request
   * @return ProblemDetail with error information
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ProblemDetail handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, WebRequest request) {
    log.error("HttpMessageNotReadableException: {}", ex.getMessage());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        "Request body is missing or malformed. Please provide a valid JSON payload."
    );

    problemDetail.setTitle("Bad Request");
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("errorCategory", "INVALID_REQUEST_BODY");

    return problemDetail;
  }

  /**
   * Handle validation errors.
   * Returns a 400 Bad Request with Problem Detail.
   *
   * @param ex The exception that was thrown
   * @param request The web request
   * @return ProblemDetail with error information
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationException(
      MethodArgumentNotValidException ex, WebRequest request) {
    log.error("Validation error: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        "Validation failed for one or more fields"
    );

    problemDetail.setTitle("Validation Error");
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("errorCategory", "VALIDATION_ERROR");
    problemDetail.setProperty("validationErrors", errors);

    return problemDetail;
  }

  /**
   * Handle IllegalArgumentException (e.g., invalid format).
   * Returns a 400 Bad Request with Problem Detail.
   *
   * @param ex The exception that was thrown
   * @param request The web request
   * @return ProblemDetail with error information
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    log.error("IllegalArgumentException: {}", ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.BAD_REQUEST,
        ex.getMessage()
    );

    problemDetail.setTitle("Bad Request");
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("errorCategory", "VALIDATION_ERROR");

    return problemDetail;
  }

  /**
   * Handle RuntimeException (general runtime errors).
   * Returns a 500 Internal Server Error with Problem Detail.
   *
   * @param ex The exception that was thrown
   * @param request The web request
   * @return ProblemDetail with error information
   */
  @ExceptionHandler(RuntimeException.class)
  public ProblemDetail handleRuntimeException(
      RuntimeException ex, WebRequest request) {
    log.error("RuntimeException: {}", ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR,
        ex.getMessage()
    );

    problemDetail.setTitle("Internal Server Error");
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("errorCategory", "RUNTIME_ERROR");

    return problemDetail;
  }

  /**
   * Handle all other exceptions.
   * Returns a 500 Internal Server Error with Problem Detail.
   *
   * @param ex The exception that was thrown
   * @param request The web request
   * @return ProblemDetail with error information
   */
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGlobalException(
      Exception ex, WebRequest request) {
    log.error("Unhandled exception: {}", ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "An unexpected error occurred: " + ex.getMessage()
    );

    problemDetail.setTitle("Internal Server Error");
    problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
    problemDetail.setProperty("errorCategory", "UNEXPECTED_ERROR");
    problemDetail.setProperty("exceptionType", ex.getClass().getSimpleName());

    return problemDetail;
  }
}
