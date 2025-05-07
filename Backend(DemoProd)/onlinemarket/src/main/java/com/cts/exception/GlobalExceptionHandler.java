package com.cts.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// Global Exception Handler class
@ControllerAdvice
public class GlobalExceptionHandler {

	// Handles UserNotFoundException
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	// Handles InvalidCredentialsException
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}

	// Handles AgeValidationException
	@ExceptionHandler(AgeValidationException.class)
	public ResponseEntity<String> handleAgeValidationException(Exception ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	// Handles PhotoSizeValidationException
	@ExceptionHandler(PhotoSizeValidationException.class)
	public ResponseEntity<String> handlePhotoSizeValidationException(Exception ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	// Handles InvalidInputException
	@ExceptionHandler(InvalidInputException.class)
	public ResponseEntity<String> handleInvalidInputException(Exception ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	// Handles EmailNotVerifiedException
	@ExceptionHandler(EmailNotVerifiedException.class)
	public ResponseEntity<String> handleEmailNotVerifiedException(EmailNotVerifiedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
	}

	// Handles DuplicateEmailException
	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<String> handleDuplicateEmailException(EmailNotVerifiedException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	// Handles InvalidEmailFormatException
	@ExceptionHandler(InvalidEmailFormatException.class)
	public ResponseEntity<String> handleInvalidEmailFormatException(EmailNotVerifiedException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(InvalidSubscriptionException.class)
	public ResponseEntity<String> handleInvalidSubscriptionException(InvalidSubscriptionException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}


	// Handles Invalid Product exceptions for subscriptions
	@ExceptionHandler(InvalidProductException.class)
	public ResponseEntity<String> handleInvalidProductException(InvalidProductException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	//Handles Invalid Search Criteria exceptions
	@ExceptionHandler(InvalidSearchCriteriaException.class)
	public ResponseEntity<String> handleInvalidSearchCriteriaException(InvalidSearchCriteriaException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	// Handles all other exceptions
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleAllExceptions(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        String name = ex.getName();
        String type = ex.getRequiredType().getSimpleName();
        String message = String.format("Fill the required field properly for parameter '%s'. Expected type: %s", name, type);

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing field(s): " + errorMessage);
    }
	
	@ExceptionHandler(HttpMessageNotReadableException.class)  
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request format.  Expected JSON.");
    }
	
}
