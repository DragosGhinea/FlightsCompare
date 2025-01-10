package com.example.FlightsCompare.model.dto;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
public class LoginRequestDto {
    private String password;
    private String email;
}
