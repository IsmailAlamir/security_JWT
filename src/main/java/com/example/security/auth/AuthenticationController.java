package com.example.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(authenticationService.register(request));
    }
    // It receives a RegisterRequest object in the request body 
    // and returns a ResponseEntity<AuthenticationResponse> containing the response from the service.register() method.



    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticateRequest request
    ){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
    // It receives an AuthenticateRequest object in the request body
    // and returns a ResponseEntity<AuthenticationResponse> containing the response from the service.authenticate() method.


//    @PostMapping("/refresh")
//    public ResponseEntity<AuthenticationResponse> refresh(
//            @RequestBody AuthenticateRequest request
//    ){
//        return ResponseEntity.ok(authenticationService.refresh(request.getRefreshToken()));
//    }
        @PostMapping("/refresh")
        public void refresh( // you ask for anything
                HttpServletRequest request, //
                HttpServletResponse response // help to send back the response
        ) throws IOException {
        authenticationService.refreshToken(request, response);
        }
}
