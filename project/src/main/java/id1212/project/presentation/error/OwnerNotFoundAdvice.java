package id1212.project.presentation.error;

import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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

	  @Override
	  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, 
			  																HttpHeaders headers, 
			  																HttpStatus status, WebRequest request) {
	      CustomErrorResponse errors = new CustomErrorResponse();
	    	errors.setTimestamp(LocalDateTime.now());
	    	errors.setError(ex.getLocalizedMessage());
	    	errors.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
	    return new ResponseEntity<Object>(errors, HttpStatus.NOT_FOUND);
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
		  errors.setStatus(HttpStatus.NOT_FOUND.value());
		  return new ResponseEntity<Object>(errors, HttpStatus.NOT_FOUND);
		  
		  
	  }
	  
	 }




