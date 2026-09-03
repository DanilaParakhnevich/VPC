package by.parakhnevich.user.repository;

import by.parakhnevich.user.domain.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by agallochum on 2026-09-03
 */
@QuarkusTest
class UserRepositoryTest {

    @Inject
    UserRepository repository;

    @Test
    @Transactional
    public void shouldSaveAndFindUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("hashed");
        repository.persist(user);

        var found = repository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }
}