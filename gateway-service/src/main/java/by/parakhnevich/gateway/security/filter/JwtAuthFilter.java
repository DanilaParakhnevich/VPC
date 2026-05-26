package by.parakhnevich.gateway.security.filter;

import by.parakhnevich.dto.response.UserResponse;
import by.parakhnevich.gateway.kafka.producer.UserRequestProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * Created by agallochum on 2026-05-12
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger(JwtAuthFilter.class);

    @Autowired
    private UserRequestProducer userRequestProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @NullMarked
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);
        if (token == null) {
            sendError(response, "No token provided", HttpStatus.UNAUTHORIZED);
            return;
        }

        try {
            UserResponse validationResult;
       
            validationResult = validateTokenViaKafka(token);
            
            if (validationResult.getErrorMessage().equals(UserResponse.ErrorMessage.NONE)) {
                UsernamePasswordAuthenticationToken authentication = createAuthentication(validationResult);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                LOGGER.debug("User authenticated: {}", validationResult.getUsername());

                filterChain.doFilter(request, response);
            } else {
                sendError(response, validationResult.getErrorMessage().toString(), HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            LOGGER.error("Authentication error", e);
            sendError(response, "Authentication failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/api/users/") ||
                path.startsWith("/public/") ||
                path.startsWith("/actuator/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/");
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
    

    private UserResponse validateTokenViaKafka(String token) throws Exception {
        CompletableFuture<UserResponse> future = userRequestProducer.validateToken(token);
        return future.get();
    }

    private UsernamePasswordAuthenticationToken createAuthentication(UserResponse validation) {
        //  TODO remove?
        /* authentication.setDetails(Map.of(
                "userId", validation.getUserId(),
                "email", validation.getEmail(),
                "tokenValid", true
        ));*/

        return new UsernamePasswordAuthenticationToken(
                validation.getUsername(),
                null,
                List.of(new SimpleGrantedAuthority(validation.getRole()))
        );
    }

    private Key getSigningKey() {
        // TODO need to remove?
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    private void sendError(HttpServletResponse response, String message, HttpStatus status)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");

        Map<String, Object> errorBody = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "path", "" // можно добавить request path если нужно
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}