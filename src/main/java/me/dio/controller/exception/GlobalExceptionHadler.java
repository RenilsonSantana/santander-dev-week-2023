package me.dio.controller.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import me.dio.service.exception.BusinessException;
import me.dio.service.exception.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHadler {
	
	private final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHadler.class);
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<String> handlebBusinessException(BusinessException businessException) {
		return new ResponseEntity<String>(businessException.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<String> handleNotFoundException(NotFoundException notFoundException) {
		return new ResponseEntity<String>("Resource ID not found.", HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(Throwable.class)
	public ResponseEntity<String> handleUnexpectedException(Throwable unexpectedException) {
		var message = "Unexpected server error, see the logs.";
		LOGGER.error(message, unexpectedException);
		return new ResponseEntity<String>(message, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
