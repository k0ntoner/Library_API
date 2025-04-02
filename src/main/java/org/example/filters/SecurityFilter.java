package org.example.filters;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.example.configs.DIContainer;
import org.example.configs.security.Auth0Config;
import org.example.enums.Role;
import org.example.services.UserService;
import org.example.utils.JwtUtil;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.auth0.jwt.RegisteredClaims.ISSUER;

@WebFilter("/*")
@Slf4j
public class SecurityFilter implements Filter {
    private UserService userService;

    private static final String AUTH0_DOMAIN = Auth0Config.getProperty("AUTH0_DOMAIN");
    private static final String CLIENT_ID = Auth0Config.getProperty("CLIENT_ID_LOG");
    private static final String CLIENT_SECRET = Auth0Config.getProperty("CLIENT_SECRET_LOG");
    private static final String CONNECTION = Auth0Config.getProperty("CONNECTION");
    private static final String AUDIENCE = Auth0Config.getProperty("AUDIENCE");

    private final JwkProvider provider = new JwkProviderBuilder(AUTH0_DOMAIN)
            .cached(10, 24, TimeUnit.HOURS)
            .build();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        DIContainer container = (DIContainer) filterConfig.getServletContext().getAttribute(DIContainer.class.getName());
        this.userService = container.getSingleton(UserService.class);
        log.info("SecurityFilter initialized successfully");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        if (!requestURI.contains("/auth") && !"/library".equals(requestURI)) {
            String token = null;

            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            if (token == null) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    token = (String) session.getAttribute("auth_token");
                }
            }
            if (token != null) {
                try {
                    DecodedJWT jwt = JWT.decode(token);
                    Jwk jwk = provider.get(jwt.getKeyId());
                    Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
                    JWTVerifier verifier = JWT.require(algorithm)
                            .withIssuer(AUTH0_DOMAIN + "/")
                            .build();
                    verifier.verify(token);

                    String userId = jwt.getSubject();
                    request.setAttribute("user_id", userId);

                    List<Role> roles = JwtUtil.extractRolesFromToken(token);

                    if ((requestURI.contains("/create") || requestURI.contains("/update") || requestURI.contains("/delete")) && !requestURI.contains("/users/update")) {
                        if (!roles.contains(Role.ROLE_EMPLOYEE)) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: insufficient permissions to access this resource.");
                            return;
                        }
                    }

                    filterChain.doFilter(request, response);
                    return;

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is not valid");
                    return;
                }
            }

            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
