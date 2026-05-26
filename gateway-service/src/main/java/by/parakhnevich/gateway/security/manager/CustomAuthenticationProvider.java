package by.parakhnevich.gateway.security.manager;

import by.parakhnevich.gateway.kafka.producer.UserRequestProducer;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


/**
 * Created by agallochum on 2026-05-16
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRequestProducer userRequestProducer;

    @Override
    public Authentication authenticate(@NonNull Authentication authentication)
            throws AuthenticationException {
        try {
            return new UsernamePasswordAuthenticationToken(
                    authentication.getName(),
                    null,
                    authentication.getAuthorities()
            );

        } catch (Exception e) {
            throw new AuthenticationServiceException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(@NonNull Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}