package com.netcracker.controller;

import com.netcracker.model.DTO.MessageDTO;
import com.netcracker.model.entity.PasswordResetToken;
import com.netcracker.service.mail.interfaces.MailSending;
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
    private MailSending mailSending;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> resetPassword(HttpServletRequest request, @RequestParam("email") String userEmail){
        PasswordResetToken passwordResetToken = passwordResetService.resetPassword(userEmail);
        if(passwordResetToken==null){
            return new ResponseEntity<>(new MessageDTO("user with email :" +userEmail+ "not exist"), HttpStatus.BAD_REQUEST);
        }
        mailSending.send(userEmail, "Reset Password", buildMailBody(buildLink(request, passwordResetToken.getToken())));
        return new ResponseEntity<>(passwordResetToken, HttpStatus.OK);
    }



    private String buildMailBody(String link){
        StringBuilder body = new StringBuilder("Link for password recover \n");
        body.append(link);
        body.append("\nBest Regards \nOfficeManagement team");
        return body.toString();
    }

    private String buildLink(HttpServletRequest request, String token){
        StringBuilder link = new StringBuilder(request.getScheme().concat("://").concat(request.getServerName()).concat(request.getContextPath()));
        link.append("/resetPassword")
                .append("/"+token);
        return link.toString();
    }
}
