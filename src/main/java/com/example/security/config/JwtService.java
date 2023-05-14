package com.example.security.config;
import com.example.security.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service // help to manage beans
public class JwtService {


    @Value("${jwt.secret}")
    private String secretKey ;


//    @Value("${jwt.token.expiration.access}")
//    private long accessTokenExpiration;
//
//    @Value("${jwt.token.expiration.refresh}")
//    private long refreshTokenExpiration;


//    private static final String SECRET_KEY = "4D6351655468576D5A7134743777217A25432A462D4A614E645267556A586E32";
   // put it inside of application.properties

    // to generate it : https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx
    // min = 256 bit or more , hex
    // you can add it to propagates

    private String accessToken;
    private String refreshToken;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;

    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;

    }
    public String extractUserEmail(String token) {

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
                .setSubject(((User) userDetails).getEmail()) // sub: "email"
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24)) // one day
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String email= extractUserEmail(token);
        return (email.equals(((User) userDetails).getEmail())) && !isTokenExpired(token);

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
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    //signing Key : is used to make signature part in jwt to make sure that the token doesn't change or someone sees it


    // refresh Token



    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(((User) userDetails).getEmail()) // sub: "email"
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24*30 )) //one month
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }


}


