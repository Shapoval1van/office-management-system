package com.netcracker.controller;


import com.netcracker.exception.*;
import com.netcracker.exception.IllegalAccessException;
import com.netcracker.exception.request.RequestNotAssignedException;
import com.netcracker.exception.requestGroup.CannotUpdateStatusException;
import com.netcracker.exception.requestGroup.RequestGroupAlreadyExist;
import com.netcracker.model.dto.ErrorDTO;
import com.netcracker.model.dto.ErrorsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@RestController
public class GlobalExceptionHandlerController {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorsDTO resourceNotFoundExceptionHandler(HttpServletRequest request, ResourceNotFoundException ex) {
        ErrorDTO error = new ErrorDTO(HttpStatus.NOT_FOUND.value(), request.getRequestURL().toString(), ex.getMessage(), ex.getDescription());
        return new ErrorsDTO(Collections.singletonList(error));
    }

    @ExceptionHandler({BadRequestException.class, CannotCreateSubRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO BadRequestExceptionHandler(HttpServletRequest request, BaseException ex) {
        ErrorDTO error = new ErrorDTO(HttpStatus.BAD_REQUEST.value(), request.getRequestURL().toString(), ex.getMessage(), ex.getDescription());
        return new ErrorsDTO(Collections.singletonList(error));
    }


    @ExceptionHandler(NotDataForThisRoleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO NotDataForThisRoleExceptionHandler(HttpServletRequest request, NotDataForThisRoleException ex) {
        int errorStatus = HttpStatus.NOT_FOUND.value();
        String title = ex.getMessage();
        String desc = ex.getDescription();
        ErrorDTO errorDTO = new ErrorDTO(errorStatus, request.getRequestURL().toString(), title, desc);
        return new ErrorsDTO(Collections.singletonList(errorDTO));
    }


    @ExceptionHandler(CurrentUserNotPresentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorsDTO currentUserNotPresentExceptionHandler(HttpServletRequest request, CurrentUserNotPresentException e) {
        int errorStatus = HttpStatus.NOT_FOUND.value();
        String source = request.getRequestURL().toString();
        String title = e.getMessage();
        String description = e.getDescription();

        ErrorDTO errorDTO = new ErrorDTO(errorStatus, source, title, description);
        return new ErrorsDTO(Collections.singletonList(errorDTO));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO handle(HttpServletRequest request, MethodArgumentNotValidException ex) {

        List<ErrorDTO> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorDTO(HttpStatus.BAD_REQUEST.value(),
                        request.getRequestURL().toString(),
                        "Validation failed.",
                        fieldError.getField().concat(" - ").concat(fieldError.getDefaultMessage())))
                .collect(Collectors.toList());

        return new ErrorsDTO(errors);
    }

    @ExceptionHandler(CannotDeleteNotificationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO cannotDeleteNotificationException(HttpServletRequest request, CurrentUserNotPresentException e) {
        int errorStatus = HttpStatus.NOT_FOUND.value();
        String title = e.getMessage();
        String source = request.getRequestURL().toString();
        String description = e.getDescription();
        ErrorDTO errorDTO = new ErrorDTO(errorStatus, source, title, description);
        return new ErrorsDTO(Collections.singletonList(errorDTO));
    }

    @ExceptionHandler({CannotCreateRequestException.class, CannotAssignRequestException.class,
            CannotDeleteRequestException.class, IncorrectStatusException.class, CannotUnassignRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO requestExceptionHandler(HttpServletRequest request, BaseException ex) {
        ErrorDTO error = new ErrorDTO(HttpStatus.NOT_FOUND.value(), request.getRequestURL().toString(), ex.getMessage(), ex.getDescription());
        return new ErrorsDTO(Collections.singletonList(error));
    }

    @ExceptionHandler({IllegalAccessException.class, CannotUpdateStatusException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorsDTO illegalAccessExceptionHandler(HttpServletRequest request, BaseException e) {
        int errorStatus = HttpStatus.FORBIDDEN.value();
        String source = request.getRequestURL().toString();
        String title = e.getMessage();
        String description = e.getDescription();

        ErrorDTO errorDTO = new ErrorDTO(errorStatus, source, title, description);
        return new ErrorsDTO(Collections.singletonList(errorDTO));
    }

    @ExceptionHandler({RequestGroupAlreadyExist.class, RequestNotAssignedException.class, CannotUpdatePersonException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDTO requestGroupExceptionsHandler(HttpServletRequest request, BaseException e) {
        int errorStatus = HttpStatus.BAD_REQUEST.value();
        String source = request.getRequestURL().toString();
        String title = e.getMessage();
        String description = e.getDescription();

        ErrorDTO errorDTO = new ErrorDTO(errorStatus, source, title, description);
        return new ErrorsDTO(Collections.singletonList(errorDTO));
    }
}
