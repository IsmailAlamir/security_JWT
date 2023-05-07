package com.example.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service // help to manage beans
public class JwtService {

    private static final String SECRET_KEY = "4D6351655468576D5A7134743777217A25432A462D4A614E645267556A586E32";
    // to generate it : https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx
    // min = 256 bit or more , hex
    // you can add it to propagates
    public String extractUsername(String token) {
        return null;
    }
    private Claims extractAllClaims (String token){ //extract  jwt
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignKey()) //we need it to generate key
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

//signing Key : is used to make signature part in jwt to make sure that the token doesn't change or someone sees it
