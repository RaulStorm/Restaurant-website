package org.example.restaurantwebsite.service;

import jakarta.transaction.Transactional;
import org.example.restaurantwebsite.model.User;
import org.example.restaurantwebsite.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Transactional
    public String registerUser(String name, String email, String password) {
        logger.info("Регистрация пользователя с именем: {}", name);

        if (userRepository.findByEmail(email).isPresent()) {
            logger.warn("Попытка регистрации с уже существующим email: {}", email);
            return null; // Пользователь с таким email уже есть
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password)); // Шифруем пароль
        userRepository.save(newUser);

        return generateToken(email);
    }

    public String authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return generateToken(email);
            }
        }
        return null;
    }

    private String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 день
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    // Метод для добавления токена в чёрный список
    public void invalidateToken(String token) {
        blacklistedTokens.add(token);
    }

    // Метод для проверки, заблокирован ли токен
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}


