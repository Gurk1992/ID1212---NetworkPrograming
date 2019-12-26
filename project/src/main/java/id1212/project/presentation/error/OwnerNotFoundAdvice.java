package id1212.project.presentation.error;

import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ControllerAdvice
public class OwnerNotFoundAdvice extends ResponseEntityExceptionHandler {


	  @ExceptionHandler(ErrorException.class)
	 public ResponseEntity<CustomErrorResponse> ownerNotFoundHandler(ErrorException ex) {
		  CustomErrorResponse errors =new CustomErrorResponse();
		  errors.setTimestamp(LocalDateTime.now());
		  errors.setError(ex.getMessage());
		 
		  errors.setStatus(HttpStatus.NOT_FOUND.value());
	    	return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
	  }
	  @ExceptionHandler({ ConstraintViolationException.class })
	  public ResponseEntity<Object> handleConstraintViolation(
	    ConstraintViolationException ex, WebRequest request) {
	      StringBuilder errors = new StringBuilder();
	      for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
	          errors.append((violation.getRootBeanClass().getName() + " " + 
	            violation.getPropertyPath() + ": " + violation.getMessage()));
	      }
	   
	      CustomErrorResponse error =new CustomErrorResponse();
		  error.setTimestamp(LocalDateTime.now());
		  error.setError(errors.toString());
		  error.setStatus(HttpStatus.BAD_REQUEST.value());
		  return new ResponseEntity<Object>(error, HttpStatus.BAD_REQUEST);
	  }
	
	  @Override
	  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, 
			  																HttpHeaders headers, 
			  																HttpStatus status, WebRequest request) {
	      CustomErrorResponse errors = new CustomErrorResponse();
	    	errors.setTimestamp(LocalDateTime.now());
	    	errors.setError(ex.getLocalizedMessage());
	    	errors.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
	    return new ResponseEntity<Object>(errors, HttpStatus.METHOD_NOT_ALLOWED);
	  }
	  @Override
	  protected ResponseEntity<Object> handleNoHandlerFoundException(
	    NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
	      CustomErrorResponse errors =new CustomErrorResponse();
		  errors.setTimestamp(LocalDateTime.now());
		  errors.setError(ex.getLocalizedMessage());
		  errors.setStatus(HttpStatus.NOT_FOUND.value());
	      return new ResponseEntity<Object>(errors, HttpStatus.NOT_FOUND);
	  }
	  
	  @Override
	  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, 
			  															HttpHeaders headers,
			  															HttpStatus status, WebRequest request) {
	     	   
	      CustomErrorResponse errors = new CustomErrorResponse();
	      errors.setTimestamp(LocalDateTime.now());
		  errors.setError(ex.getLocalizedMessage() + ". Supported Content-type type is application/json");
		  errors.setStatus( HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
		 
	       
		  return new ResponseEntity<Object>(errors, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	  }
	  @Override
	    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
	                                                                  HttpHeaders headers,
	                                                                  HttpStatus status, WebRequest request) {
		  List<String> errorMsgs = ex.getBindingResult()
	                .getFieldErrors()
	                .stream()
	                .map(x -> x.getDefaultMessage())
	                .collect(Collectors.toList());
		  CustomErrorResponse errors =new CustomErrorResponse();
		  errors.setTimestamp(LocalDateTime.now());
		  errors.setError(errorMsgs.toString());
		  errors.setStatus(HttpStatus.BAD_REQUEST.value());
		  return new ResponseEntity<Object>(errors, HttpStatus.BAD_REQUEST);
		  
		  
	  }
	  @Override
	  protected ResponseEntity<Object> handleMissingServletRequestParameter(
	    MissingServletRequestParameterException ex, HttpHeaders headers, 
	    HttpStatus status, WebRequest request) {
	      String errorMsgs = ex.getParameterName() + " parameter is missing";
	       
	      CustomErrorResponse errors =new CustomErrorResponse();
		  errors.setTimestamp(LocalDateTime.now());
		  errors.setError(errorMsgs.toString());
		  errors.setStatus(HttpStatus.BAD_REQUEST.value());
		  return new ResponseEntity<Object>(errors, HttpStatus.BAD_REQUEST);
	  }
	  
	 }




