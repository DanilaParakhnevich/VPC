package by.parakhnevich.gateway.security.manager;

import by.parakhnevich.dto.response.UserResponse;
import by.parakhnevich.gateway.service.producer.UserCommandProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by agallochum on 2026-05-16
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserCommandProducer userCommandProducer;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            var response = userCommandProducer.authenticate(username, password)
                    .get(5, TimeUnit.SECONDS); // Блокируемся, пока ждем ответ

            if (response.getStatus().equals(UserResponse.Status.OK)) {
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(response.getRole()));

                return new UsernamePasswordAuthenticationToken(
                        response.getUsername(),
                        null,
                        authorities
                );
            } else {
                throw new BadCredentialsException(response.getErrorMessage().getMessage());
            }

        } catch (TimeoutException e) {
            throw new AuthenticationServiceException("Authentication service timeout");
        } catch (Exception e) {
            throw new AuthenticationServiceException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}