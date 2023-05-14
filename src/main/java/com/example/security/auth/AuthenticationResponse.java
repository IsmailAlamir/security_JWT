package com.example.security.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    
    @JsonProperty("access_Token") //to specify the JSON property name for a field or method during serialization and deserialization
    private String accessToken;

    @JsonProperty("refresh_Token")
    private String refreshToken;

}


