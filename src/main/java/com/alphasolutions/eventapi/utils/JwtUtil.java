package com.alphasolutions.eventapi.utils;


import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.model.User;
import com.alphasolutions.eventapi.repository.UserRepository;
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
        claims.put("email", user.getEmail());
        claims.put("nome", user.getNome());
        claims.put("role", user.getRole().getRole());
        claims.put("unique_code", user.getUniqueCode());
        claims.put("avatar_seed", user.getAvatarSeed());
        claims.put("avatar_style", user.getAvatarStyle());
        claims.put("id", user.getId()); 
        
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + 3600 * 1000 * 24 * 7))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            
            // Validação mais robusta
            if (claims.get("avatar_seed") == null || 
                claims.get("avatar_style") == null ||
                claims.getSubject() == null) {
                throw new InvalidTokenException("Claims essenciais faltando");
            }
            
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Map<String, Object> extractClaim(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            // Retorna todos os claims diretamente (incluindo subject e expiration)
            return new HashMap<>(claims);
        } catch (Exception e) {
            throw new InvalidTokenException("Token inválido ou expirado");
        }
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
