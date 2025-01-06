package com.example.FlightsCompare.service.impl;

import com.example.FlightsCompare.exception.ClientRegistrationNotFound;
import com.example.FlightsCompare.model.User;
import com.example.FlightsCompare.model.dto.OAuth2UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2ToUserServiceCentralized {
    private final GithubToUserServiceImpl githubToUserServiceImpl;
    private final DiscordToUserServiceImpl discordToUserServiceImpl;


    public User getUserFromOAuth2(OAuth2UserRequestDto oAuth2UserRequestDTO) {
        return switch (oAuth2UserRequestDTO.getClientRegistrationId()) {
            case "github" -> githubToUserServiceImpl.getUserFromOAuth2(oAuth2UserRequestDTO.getAccessToken());

            case "discord" -> discordToUserServiceImpl.getUserFromOAuth2(oAuth2UserRequestDTO.getAccessToken());

            default -> throw new ClientRegistrationNotFound("Client registration "+oAuth2UserRequestDTO.getClientRegistrationId()+" not found!");
        };
    }
}