package com.example.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // to tell spring this is beans , allows it to be automatically detected and registered as a bean 
@RequiredArgsConstructor //create a constructor using any final field "generates a constructor with required arguments"
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; //our own implementations because we want to fetch our user from our Database

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
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails= this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt,userDetails)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request,response);
    }
}
