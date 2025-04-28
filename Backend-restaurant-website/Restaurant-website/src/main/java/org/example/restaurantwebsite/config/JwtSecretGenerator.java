package org.example.restaurantwebsite.config;

import io.jsonwebtoken.security.Keys;

import java.util.Base64;
import javax.crypto.SecretKey;

public class JwtSecretGenerator {
    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
        String secret = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Сгенерированный ключ: " + secret);
    }
}
