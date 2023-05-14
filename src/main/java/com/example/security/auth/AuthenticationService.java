package com.example.security.auth;


import com.example.security.config.JwtService;
import com.example.security.user.UesrRepository;
import com.example.security.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service

@RequiredArgsConstructor
public class AuthenticationService {

    private final UesrRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthenticationResponse register(RegisterRequest request) {
        var user= User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticateRequest request) { // the user can log in using email or username
        User user;

        if (request.getEmail() == null) {
            user = repository.findByUsername(request.getUsername())
                    .orElseThrow();
        } else {
            user = repository.findByEmail(request.getEmail())
                    .orElseThrow();
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        request.getPassword()
                )
        );

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

//    public AuthenticationResponse authenticate(AuthenticateRequest request) { // log in using email only
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );
//        var user = repository.findByEmail(request.getEmail())
//                .orElseThrow();
//
//
//        var accessToken = jwtService.generateToken(user);
//        var refreshToken = jwtService.generateRefreshToken(user);
//        return AuthenticationResponse.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//    }

    public AuthenticationResponse refresh(String refreshToken) {
        String email = jwtService.extractUserEmail(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (jwtService.isTokenValid(refreshToken, userDetails) && userDetails != null) {
            String newAccessToken = jwtService.generateToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            return AuthenticationResponse
                    .builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
        }
        return null;
    }


    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        // when you make a call you pass jwt in header , pass header name "Authorization"
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }

        refreshToken =authHeader.substring(7);//7 = number of "Bearer "
        userEmail = jwtService.extractUserEmail(refreshToken);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails= this.repository.findByEmail(userEmail)
                        .orElseThrow();
                if (jwtService.isTokenValid(refreshToken,userDetails)) {
                    var accessToken= jwtService.generateToken(userDetails);
                    var authResponse= AuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                    new ObjectMapper().writeValue(response.getOutputStream(),authResponse)
                }
                }



    }
}
