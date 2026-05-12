package by.parakhnevich.user.service;

import by.parakhnevich.user.domain.entity.User;

import java.util.Date;

public interface JwtService {

    String generateToken(String username);

    boolean validateToken(String token, User user);

    String extractUsername(String token);

    Date extractExpiration(String token);
}
