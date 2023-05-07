package com.example.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // to tell spring this is beans
@RequiredArgsConstructor //create a constructor using any final field
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    @Override
    protected void doFilterInternal( //filters
            @NonNull HttpServletRequest request,// our req
            @NonNull HttpServletResponse response,// our res
            @NonNull FilterChain filterChain // contain others filter

        ) throws ServletException, IOException { //check jwt tokens
            final String authHeader = request.getHeader("Authorization");
            // when you make a call you pass jwt in header , pass header name "Authorization"
            final String jwt;
            final String userEmail;
            if (authHeader == null || !authHeader.startsWith("Bearer ")){
                filterChain.doFilter(request,response);// first filter
                return;
            }
            jwt =authHeader.substring(7);//7 = number of "Bearer "
            userEmail = jwtService.extractUsername(jwt);// todo extract the userEmail from JWT token ;
    }
}
