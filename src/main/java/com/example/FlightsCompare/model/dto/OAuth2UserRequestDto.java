package com.example.FlightsCompare.model.dto;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
public class OAuth2UserRequestDto {
    private String clientRegistrationId;
    private String accessToken;
}