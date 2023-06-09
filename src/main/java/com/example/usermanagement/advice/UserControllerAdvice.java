package com.example.usermanagement.advice;

import com.example.usermanagement.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class UserControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(){
        return new ResponseEntity<>("User not found!. Check your given user ID", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElement(){
        return new ResponseEntity<>("Element not Found. Please check the URL", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFound(){
        return new ResponseEntity<>("Username does not exists.", HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<String> handleSQLIntegrityViolation(){
        return new ResponseEntity<>("Must have unique mobile number and username.", HttpStatus.NOT_FOUND);
    }

}
