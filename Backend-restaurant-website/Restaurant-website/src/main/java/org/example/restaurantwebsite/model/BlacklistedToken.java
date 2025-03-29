package org.example.restaurantwebsite.model;

import jakarta.persistence.*;
<<<<<<< HEAD
import java.util.Date;

=======
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
>>>>>>> ec63c2eed480bf7ae719c2ab3fa86027dac8f1f8
@Entity
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {

<<<<<<< HEAD
=======
    // Геттеры и сеттеры
>>>>>>> ec63c2eed480bf7ae719c2ab3fa86027dac8f1f8
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500) // JWT токены могут быть длинными
    private String token;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiration;

<<<<<<< HEAD
    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
=======
>>>>>>> ec63c2eed480bf7ae719c2ab3fa86027dac8f1f8
}
