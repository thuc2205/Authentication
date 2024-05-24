package com.example.testauthor.component;

import com.example.testauthor.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    @Value("${jwt.secretKey}")
    private String SECRET_KEY;

    @Deprecated
    public String generateToken(User user) throws Exception{
        Date now = new Date();
//            this.generateSecureKey();
        Date expiration = new Date(now.getTime() + 2592000);
        try {
            Map<String, Object> claim = new HashMap<>();
            claim.put("userName",user.getUsername());
            String token = Jwts.builder()
                    .setClaims(claim)
                    .setSubject(user.getUsername())
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        }catch (Exception e){
            throw new InvalidParameterException(e.getMessage());
        }

    }
    private Key getSignInKey(){
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);
//            Keys.hmacShaKeyFor(Decoders.BASE64.decode("j8AMGduM5Yby30zsz3ZOvsRDuLNsng/+XfSJiezSJ4o="));
        return Keys.hmacShaKeyFor(bytes);
    }
    // Hàm trích xuất tất cả các claim từ token
    @Deprecated
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (JwtException e){
            e.printStackTrace();
            return null;
        }

    }

    private String generateSecureKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[32]; // Create a byte array of the desired key size
        random.nextBytes(key);
        String secretKey = Encoders.BASE64.encode(key);
        return secretKey;
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Hàm trích xuất số điện thoại từ token
    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

}
