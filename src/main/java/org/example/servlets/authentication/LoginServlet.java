package org.example.servlets.authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.example.configs.DIContainer;
import org.example.configs.security.Auth0Config;
import org.example.controllers.UserController;
import org.example.controllers.CustomerController;
import org.example.controllers.EmployeeController;
import org.example.dtos.LoginUserDto;
import org.example.utils.ThymeleafUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@WebServlet("/auth/login")
@Slf4j
public class LoginServlet extends HttpServlet {
    private static final String AUTH0_DOMAIN = Auth0Config.getProperty("AUTH0_DOMAIN");
    private static final String CLIENT_ID = Auth0Config.getProperty("CLIENT_ID_LOG");
    private static final String CLIENT_SECRET = Auth0Config.getProperty("CLIENT_SECRET_LOG");
    private static final String CONNECTION = Auth0Config.getProperty("CONNECTION");
    private static final String AUDIENCE = Auth0Config.getProperty("AUDIENCE");

    private UserController authController;
    private CustomerController customerController;
    private EmployeeController employeeController;

    @Override
    public void init() throws ServletException {
        DIContainer container = (DIContainer) getServletContext().getAttribute(DIContainer.class.getName());
        this.authController = container.getSingleton(UserController.class);
        this.employeeController = container.getSingleton(EmployeeController.class);
        this.customerController = container.getSingleton(CustomerController.class);
        log.info("LoginServlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        log.info("LoginServlet call method doGet");
        String passInfo = req.getPathInfo();
        try {
            ThymeleafUtil.render(req, resp, getServletContext(), "auth/login", null);
        } catch (Exception e) {
            log.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("LoginServlet call method doPost");
        LoginUserDto user = mapToLoginUser(request);
        try {
            String token = authenticationUserInAuth0(user.getEmail(), user.getPassword());

            request.getSession().setAttribute("auth_token", token);
            response.sendRedirect(request.getContextPath() + "/users/user ");
        } catch (IllegalArgumentException e) {
            log.warn("Invalid credentials to Auth0: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid password or email");
        } catch (BadRequestException e) {
            log.warn("Bad request to Auth0: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request to authentication provider");
        } catch (IOException e) {
            log.error("Internal server error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error");
        }

    }

    private LoginUserDto mapToLoginUser(HttpServletRequest request) {
        return LoginUserDto.builder().email(request.getParameter("email")).password(request.getParameter("password")).build();
    }

    private String authenticationUserInAuth0(String email, String password) throws IOException {
        URL url = new URL(AUTH0_DOMAIN + "/oauth/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String requestBody = String.format("""
                {
                    "grant_type": "password",
                    "client_id": "%s",
                    "client_secret": "%s",
                    "username": "%s",
                    "password": "%s",
                    "audience": "%s",
                    "scope": "openid profile email",
                    "realm": "Username-Password-Authentication"
                }
                """, CLIENT_ID, CLIENT_SECRET, email, password, AUDIENCE);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        String responseBody = null;

        InputStream stream = (responseCode >= 200 && responseCode < 300) ? connection.getInputStream() : connection.getErrorStream();

        if (responseCode == 200) {
            String jsonResponse = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return new JSONObject(jsonResponse).getString("access_token");
        }

        if (stream != null) {
            responseBody = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            log.error("Auth0 response [{}]: {}", responseCode, responseBody);
        }

        if (responseCode == 400) {
            throw new BadRequestException(responseBody);
        } else if (responseCode == 403) {
            throw new IllegalArgumentException(responseBody);
        } else {
            throw new IOException("Unexpected response from Auth0: " + responseCode);
        }
    }
}
