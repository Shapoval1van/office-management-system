package com.netcracker.controller;

import com.netcracker.exception.OutdatedTokenException;
import com.netcracker.model.dto.MessageDTO;
import com.netcracker.model.dto.PersonDTO;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Token;
import com.netcracker.service.resetPassword.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/v1/resetPassword")
@Validated
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> resetPassword(HttpServletRequest request,
                                           @Pattern(regexp = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")
                                           @RequestParam("email") String userEmail){
        Token passwordResetToken = passwordResetService.resetPassword(userEmail, buildLink(request));
        if(passwordResetToken==null){
            return new ResponseEntity<>(new MessageDTO("user with email: " +userEmail+ " not exist"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(passwordResetToken, HttpStatus.OK);
    }

    @RequestMapping(value = "/{token}",  method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updatePassword(HttpServletRequest request, @PathVariable(required = true, name = "token") String token,
                                            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,70}$")
                                            @RequestPart("password") String password){
        try {
            Person person = passwordResetService.updatePasswordForPersonByEmail(password, token);
            if (person == null){
                return new ResponseEntity<>(new MessageDTO("Token not found"), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(new PersonDTO(person), HttpStatus.OK);
        } catch (OutdatedTokenException outdatedTokenException) {
            return new ResponseEntity<>(new MessageDTO("Token expired"), HttpStatus.BAD_REQUEST);
        }
    }

    private String buildLink(HttpServletRequest request){
        String SITE_LINK = "https://management-office.herokuapp.com";
        return SITE_LINK;
    }
}
