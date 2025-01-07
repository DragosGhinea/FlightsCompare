package com.example.FlightsCompare.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {

    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public static ErrorDto generate(String message, String uri, HttpStatus status) {
        return new ErrorDto(
                LocalDateTime.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message,
                uri
        );
    }
}