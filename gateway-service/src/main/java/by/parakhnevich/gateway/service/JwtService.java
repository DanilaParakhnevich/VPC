package by.parakhnevich.gateway.service;

import java.util.Date;

public interface JwtService {

    String generateToken(String username);

    Date extractExpiration(String token);
}
