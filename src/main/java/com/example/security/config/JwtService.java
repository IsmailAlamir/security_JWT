package com.example.security.config;

import com.example.security.user.Role;
import com.example.security.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service // help to manage beans
public class JwtService {

    private static final String SECRET_KEY = "4D6351655468576D5A7134743777217A25432A462D4A614E645267556A586E32";
    // to generate it : https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx
    // min = 256 bit or more , hex
    // you can add it to propagates
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token , Function<Claims, T> claimsResolver){
        final Claims claims=extractAllClaims(token); //extract the claims
        return claimsResolver.apply(claims);
    }
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }
    public String generateToken(
            Map<String, Object> extractClaims,
            UserDetails userDetails
    ){
        String role = ((User) userDetails).getRole().toString();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // to send the role with the token 

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername()) // sub: "username"
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username= extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
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
