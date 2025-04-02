package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.Role;

import java.util.*;

@Slf4j
public class JwtUtil {
    public static Optional<String> extractAuthId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String token = (String) session.getAttribute("auth_token");
            if (token != null) {
                DecodedJWT jwt = JWT.decode(token);
                String authId = jwt.getSubject();
                log.info("Extracted id form token: {}", authId);
                return Optional.of(authId);
            }
        }
        log.info("No session found");
        return Optional.empty();
    }

    public static List<Role> extractRolesFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Claim claim = jwt.getClaim("https://library.com/roles");

        if (claim == null || claim.isNull()) {
            return Collections.emptyList();
        }

        List<String> roleNames = claim.asList(String.class);
        if (roleNames == null) {
            return Collections.emptyList();
        }

        List<Role> roles = new ArrayList<>();
        claim.asList(String.class).forEach(role -> {
            roles.add(Role.valueOf(role));
        });
        return roles;
    }

}
