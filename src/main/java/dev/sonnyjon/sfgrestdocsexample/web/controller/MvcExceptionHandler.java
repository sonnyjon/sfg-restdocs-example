package dev.sonnyjon.sfgrestdocsexample.web.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jt on 2019-05-25.
 */
@ControllerAdvice
public class MvcExceptionHandler
{
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List> validationErrorHandler(ConstraintViolationException cve)
    {
        List<String> errorsList = new ArrayList<>(cve.getConstraintViolations().size());

        cve.getConstraintViolations()
            .forEach(error -> errorsList.add( error.toString() ));

        return new ResponseEntity<>(errorsList, HttpStatus.BAD_REQUEST);
    }

}
