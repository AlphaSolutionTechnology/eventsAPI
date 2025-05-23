package com.alphasolutions.eventapi.utils;


import com.alphasolutions.eventapi.model.entity.User;
import com.google.auth.oauth2.TokenVerifier;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.google.api.client.json.webtoken.JsonWebToken.Payload;


@Service
public class JwtUtil {

    @Value("${client.id}")
    private String clientID;

    @Value("${jwt.secret-key}")
    private String secretKey;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email",user.getEmail());
        claims.put("name",user.getNome());
        claims.put("role",user.getRole().getRole());
        claims.put("unique_code",user.getUniqueCode());

        return Jwts.builder()
                .subject(user.getIdUser())
                .issuedAt(new Date())
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + 3600 * 1000 * 24 * 7))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private boolean validateToken(String token) {
        try {
            Jwts
                .parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String,Object> extractClaim(String token){
        if(validateToken(token)) {
            try {
                Claims claims = Jwts
                        .parser()
                        .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
                Map<String, Object> claimsMap = new HashMap<>();
                claimsMap.put("id", claims.getSubject());
                claimsMap.put("email", claims.get("email"));
                claimsMap.put("name", claims.get("name"));
                claimsMap.put("role", claims.get("role"));
                claimsMap.put("unique_code", claims.get("unique_code"));
                return claimsMap;
            } catch (Exception e) {
                return Map.of("error",e.getMessage());
            }
        }
        return Map.of("error","Invalid Token");
    }

    public Payload verifyGoogleToken(String googleToken) {
        try {
            TokenVerifier tokenVerifier = TokenVerifier.newBuilder().setAudience(clientID).setIssuer("https://accounts.google.com").build();
            var verifiedToken = tokenVerifier.verify(googleToken);
            return verifiedToken.getPayload();

        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
