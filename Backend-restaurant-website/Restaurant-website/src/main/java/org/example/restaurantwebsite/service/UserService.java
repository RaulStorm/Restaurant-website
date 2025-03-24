package org.example.restaurantwebsite.service;

import org.example.restaurantwebsite.model.Role;
import org.example.restaurantwebsite.model.User;
import org.example.restaurantwebsite.repository.RoleRepository;
import org.example.restaurantwebsite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String jwtSecret = "your_jwt_secret";

    public String registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return null;  // пользователь уже существует
        }

        Role clientRole = roleRepository.findByName("CLIENT").orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(new HashSet<>(Set.of(clientRole)));

        userRepository.save(user);
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
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
