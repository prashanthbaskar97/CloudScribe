package com.prashanth.cloud.app.exception;
import jakarta.servlet.http.HttpServletRequest;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.amazonaws.services.sns.model.NotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleUncaughtException(RuntimeException exception, HttpServletRequest request) {
        String message = "Unknown error occured";
        System.out.println(exception.getStackTrace());
        for(StackTraceElement ele: exception.getStackTrace()) {
            System.out.println(ele.toString());
        }
        return buildResponseEntity(new APIError(HttpStatus.INTERNAL_SERVER_ERROR, message, exception));
    }

    @ExceptionHandler({PSQLException.class, ConnectException.class, CannotCreateTransactionException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<Object> handlePSQLException(RuntimeException exception, HttpServletRequest request) {
        return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<String> handleMethodNotAllowed(MethodNotAllowedException ex) {
        return new ResponseEntity(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalArgumentException.class, FileNotFoundException.class, NoSuchFileException.class, NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundException(Exception exception, HttpServletRequest request) {
        String message = "Requested resource could not be located";
        return buildResponseEntity(new APIError(HttpStatus.NOT_FOUND, message, exception));
    }


    private ResponseEntity<Object> buildResponseEntity(APIError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        super.handleMethodArgumentNotValid(ex, headers, status, request);
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        Map<String, List<String>> errorMap = new HashMap<>();
        errorMap.put("validationErrors", new ArrayList<>());
        for(FieldError fieldError: errors) {
            errorMap.get("validationErrors").add(fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }
}
