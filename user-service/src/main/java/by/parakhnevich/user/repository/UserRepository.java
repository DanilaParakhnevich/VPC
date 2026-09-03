package by.parakhnevich.user.repository;

import by.parakhnevich.user.domain.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.ZonedDateTime;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, Long> {

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    public void updateLastLoginAt(String username, ZonedDateTime lastLoginAt) {
        update("lastLoginAt = :lastLoginAt WHERE username = :username",
                Parameters.with("lastLoginAt", lastLoginAt).and("username", username));
    }
}