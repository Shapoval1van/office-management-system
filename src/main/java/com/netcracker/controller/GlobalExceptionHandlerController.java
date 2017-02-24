package com.netcracker.controller;


import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@RestController
public class GlobalExceptionHandlerController {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO resourceNotFoundExceptionHandler(HttpServletRequest request, ResourceNotFoundException ex){
        ErrorDTO errorDTO = new ErrorDTO(HttpStatus.NOT_FOUND.value(),request.getRequestURL().toString(),ex.getMessage(),ex.getDescription());
        return errorDTO;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorDTO> handle(HttpServletRequest request, MethodArgumentNotValidException ex) {

        List<ErrorDTO> errorDTOS = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorDTO(HttpStatus.BAD_REQUEST.value(),
                        request.getRequestURL().toString(),
                        "Validation failed.",
                        fieldError.getField().concat(" - ").concat(fieldError.getDefaultMessage())))
                .collect(Collectors.toList());

        return errorDTOS;
    }

}
