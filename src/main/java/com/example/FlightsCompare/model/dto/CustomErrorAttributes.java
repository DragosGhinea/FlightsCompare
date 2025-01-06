package com.example.FlightsCompare.model.dto;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

@Component
@Log
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @SneakyThrows
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
        if (errorAttributes.getOrDefault("error", "").equals("Internal Server Error")) {
            String message = (String) errorAttributes.getOrDefault("message", "");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String hashedMessage = Base64.getEncoder().encodeToString(digest.digest(message.getBytes()));
            errorAttributes.put("message", "Error reference: "+hashedMessage);
            log.warning("An internal server error has occured:\n"+message+"\nWith reference: "+hashedMessage);
        }

        return errorAttributes;
    }
}