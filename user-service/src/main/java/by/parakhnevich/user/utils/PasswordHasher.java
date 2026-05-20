package by.parakhnevich.user.utils;

import jakarta.enterprise.context.ApplicationScoped;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * Created by agallochum on 2026-05-16
 */
@ApplicationScoped
public class PasswordHasher {
    public String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
    public boolean matches(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}