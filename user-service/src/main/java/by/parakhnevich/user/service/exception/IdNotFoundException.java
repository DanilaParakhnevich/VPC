package by.parakhnevich.user.service.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

/**
 * Created by agallochum on 2026-05-07
 */
public class IdNotFoundException extends AuthenticationException {

    @Serial
    private static final long serialVersionUID = 1410688585992297006L;

    private static final String DEFAULT_USER_NOT_FOUND_MESSAGE = "user not found";

    private final @Nullable String name;

    public IdNotFoundException(String msg) {
        super(msg);
        this.name = null;
    }

    private IdNotFoundException(String msg, String name) {
        super(msg);
        this.name = name;
    }

    public static IdNotFoundException fromId(String id) {
        return new IdNotFoundException(DEFAULT_USER_NOT_FOUND_MESSAGE, id);
    }

    public @Nullable String getName() {
        return this.name;
    }

}
