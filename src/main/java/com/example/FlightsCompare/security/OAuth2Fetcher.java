package com.example.FlightsCompare.security;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

// inspired by DefaultOAuth2UserService
public class OAuth2Fetcher {

        private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<>() {};
        private static final MediaType DEFAULT_CONTENT_TYPE = MediaType.valueOf("application/x-www-form-urlencoded;charset=UTF-8");

        private final RestTemplate restTemplate = new RestTemplate();

        private RequestEntity<?> getRequest(HttpMethod httpMethod, String userInfoEndpointURI, String accessToken) {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            URI uri = UriComponentsBuilder.fromUriString(userInfoEndpointURI).build().toUri();
            RequestEntity<?> request;
            if (HttpMethod.POST.equals(httpMethod)) {
                headers.setContentType(DEFAULT_CONTENT_TYPE);
                MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<>();
                formParameters.add("access_token", accessToken);
                request = new RequestEntity<>(formParameters, headers, httpMethod, uri);
            } else {
                headers.setBearerAuth(accessToken);
                request = new RequestEntity<>(headers, httpMethod, uri);
            }

            return request;
        }

        public Map<String, Object> getUserAttributes(LiteClientRegistration clientRegistration, String accessToken) {
            RequestEntity<?> request = getRequest(clientRegistration.getHttpMethod(), clientRegistration.getUserEndpointURI(), accessToken);

            Map<String, Object> toReturn = this.restTemplate.exchange(request, PARAMETERIZED_RESPONSE_TYPE).getBody();
            if (toReturn == null) {
                return Collections.emptyMap();
            }

            if (toReturn.getOrDefault("email", null) == null && clientRegistration.equals(LiteClientRegistration.GITHUB)) {
                RequestEntity<?> emailsRequest = getRequest(HttpMethod.GET, "https://api.github.com/user/emails", accessToken);

                Object[] emails = this.restTemplate.exchange(emailsRequest, Object[].class).getBody();

                LinkedHashMap<String, Object> emailMap = (LinkedHashMap<String, Object>) emails[0];
                toReturn.put("email", emailMap.getOrDefault("email", null));
            }

            return toReturn;
        }
}