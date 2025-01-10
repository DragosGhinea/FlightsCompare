package com.example.FlightsCompare.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
public class RegisterRequestDto {

    private String username;
    private String password;
    private String email;
    private String accessToken;
}
