package com.netcracker.controller;

import com.netcracker.exception.OutdatedTokenException;
import com.netcracker.model.DTO.MessageDTO;
import com.netcracker.model.entity.PasswordResetToken;
import com.netcracker.model.entity.Person;
import com.netcracker.service.notification.interfaces.NotificationSender;
import com.netcracker.service.resetPassword.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/resetPassword")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private NotificationSender notificationSender;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> resetPassword(HttpServletRequest request, @RequestParam("email") String userEmail){
        PasswordResetToken passwordResetToken = passwordResetService.resetPassword(userEmail);
        if(passwordResetToken==null){
            return new ResponseEntity<>(new MessageDTO("user with email :" +userEmail+ "not exist"), HttpStatus.BAD_REQUEST);
        }
        notificationSender.sendPasswordReminder(passwordResetToken.getPerson(),buildLink(request, passwordResetToken.getToken()));
        return new ResponseEntity<>(passwordResetToken, HttpStatus.OK);
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updatePassword(HttpServletRequest request, @PathVariable(required = true, name = "token") String token,
                                            @RequestPart("password") String password){
        try {
            Person person = passwordResetService.updatePasswordForPersonByEmail(password, token);
            if (person == null){
                return new ResponseEntity<>(new MessageDTO("Token not found"), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(person, HttpStatus.OK);
        } catch (OutdatedTokenException outdatedTokenException) {
            outdatedTokenException.printStackTrace();
            return new ResponseEntity<>(new MessageDTO("Token expired"), HttpStatus.BAD_REQUEST);
        }
    }

    private String buildLink(HttpServletRequest request, String token){
        StringBuilder link = new StringBuilder(request.getScheme().concat("://").concat(request.getServerName()).concat(request.getContextPath()));
        link.append("/resetPassword")
                .append("/"+token);
        return link.toString();
    }
}
