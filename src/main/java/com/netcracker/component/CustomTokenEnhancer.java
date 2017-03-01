package com.netcracker.component;

import com.netcracker.model.entity.Person;
import com.netcracker.service.security.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomTokenEnhancer implements TokenEnhancer {

    @Autowired
    private UserDetailsService userDetailService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,OAuth2Authentication authentication) {
        Person person = (Person) userDetailService.loadUserByUsername(authentication.getName());
        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("id", person.getId());
        additionalInfo.put("firstName", person.getFirstName());
        additionalInfo.put("lastName", person.getLastName());
        additionalInfo.put("email", person.getEmail());
        additionalInfo.put("type", person.getRole().getName());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }

}