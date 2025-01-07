package com.example.FlightsCompare.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultAdmin {

    @Getter
    private static String defaultAdminEmail;

    @Value("${default_admin_email}")
    public void setDefaultAdminEmail(String email) {
        defaultAdminEmail = email;
    }

}
