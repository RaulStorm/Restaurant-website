package org.example.restaurantwebsite.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;  // Base64-encoded секрет

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // Метод, возвращающий ключ для подписи, декодируя Base64-строку
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Метод валидации токена
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            Date expiration = claimsJws.getBody().getExpiration();
            return !expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Извлечение имени пользователя (subject) из токена
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Метод создания объекта Authentication на основе токена, извлекая роли
    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        List<String> roles = claims.get("roles", List.class);
        Set<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
