package org.example.servlets.authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.configs.DIContainer;
import org.example.configs.security.Auth0Config;
import org.example.controllers.UserController;
import org.example.controllers.CustomerController;
import org.example.controllers.EmployeeController;
import org.example.entities.Customer;
import org.example.entities.Employee;
import org.example.entities.User;
import org.example.enums.Role;
import org.example.utils.ThymeleafUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@WebServlet("/auth/registration")
@Slf4j
public class RegistrationServlet extends HttpServlet {
    private static final String AUTH0_DOMAIN = Auth0Config.getProperty("AUTH0_DOMAIN");
    private static final String CLIENT_ID = Auth0Config.getProperty("CLIENT_ID_REG");
    private static final String CLIENT_SECRET = Auth0Config.getProperty("CLIENT_SECRET_REG");
    private static final String CONNECTION = Auth0Config.getProperty("CONNECTION");

    private UserController userController;
    private CustomerController customerController;
    private EmployeeController employeeController;

    @Override
    public void init() throws ServletException {
        DIContainer container = (DIContainer) getServletContext().getAttribute(DIContainer.class.getName());
        this.userController = container.getSingleton(UserController.class);
        this.employeeController = container.getSingleton(EmployeeController.class);
        this.customerController = container.getSingleton(CustomerController.class);
        log.info("Registration Servlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        log.info("RegistrationServlet call method doGet");
        String passInfo = req.getPathInfo();
        try {

            ThymeleafUtil.render(req, resp, getServletContext(), "auth/registration", null);

        } catch (Exception e) {
            log.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        log.info("RegistrationServlet call method doPost");
        try {
            boolean isPhoneExists = userController.isPhoneNumberExists(request.getParameter("phoneNumber"));
            if (isPhoneExists) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Phone number already exists");
                return;
            }
            String auth0UserId = registerUserInAuth0(request.getParameter("email"), request.getParameter("password"), Role.valueOf(request.getParameter("role")));
            User user = mapToUser(request);
            user.setId(auth0UserId);
            user = registerUserInLocalDb(user);

        } catch (IOException | SQLException e) {
            log.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private User mapToUser(HttpServletRequest request) {
        if ("ROLE_EMPLOYEE".equals(request.getParameter("role"))) {
            return Employee.builder()
                    .email(request.getParameter("email"))
                    .firstName(request.getParameter("firstName"))
                    .lastName(request.getParameter("lastName"))
                    .phoneNumber(request.getParameter("phoneNumber"))
                    .salary(BigDecimal.valueOf(Double.parseDouble(request.getParameter("salary"))))
                    .build();
        }
        if ("ROLE_CUSTOMER".equals(request.getParameter("role"))) {
            return Customer.builder()
                    .email(request.getParameter("email"))
                    .firstName(request.getParameter("firstName"))
                    .lastName(request.getParameter("lastName"))
                    .phoneNumber(request.getParameter("phoneNumber"))
                    .dateOfBirth(LocalDate.parse((request.getParameter("dateOfBirth")), DateTimeFormatter.ISO_LOCAL_DATE))
                    .build();
        }
        throw new NoSuchElementException("UserType not found");

    }

    private User registerUserInLocalDb(User user) throws IOException, SQLException {
        if (user instanceof Customer customer) {
            return customerController.createCustomer(customer);
        } else if (user instanceof Employee employee) {
            return employeeController.createEmployee(employee);
        }
        throw new NoSuchElementException("UserType not found");
    }

    private String registerUserInAuth0(String email, String password, Role role) throws IOException {
        String apiUrl = AUTH0_DOMAIN + "/api/v2/users";
        String managementToken = Auth0Config.getManagementToken();
        String requestBody = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\", \"connection\": \"%s\"}",
                email, password, CONNECTION
        );

        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + managementToken);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
            String jsonResponse = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            String userId = new JSONObject(jsonResponse).getString("user_id");

            assignRoleToUser(userId, managementToken, role);
            return userId;
        }

        InputStream errorStream = connection.getErrorStream();
        if (errorStream != null) {
            String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
            log.error("Auth0 Error: " + errorResponse);
        }
        throw new RuntimeException("Error with auth0: " + responseCode);
    }

    private void assignRoleToUser(String userId, String token, Role role) throws IOException {
        String roleId;

        if (role.equals(Role.ROLE_EMPLOYEE)) {
            roleId = "rol_65wiU5wAcQpdqnd4";
        } else if (role.equals(Role.ROLE_CUSTOMER)) {
            roleId = "rol_VgyNFq0QZbnRuIF6";
        } else {
            throw new IllegalArgumentException("Unknown user type: " + role);
        }

        String apiUrl = AUTH0_DOMAIN + "/api/v2/users/" + userId + "/roles";

        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String requestBody = String.format("{\"roles\": [\"%s\"]}", roleId);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 204) {
            String error = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            log.error("Failed to assign role: " + error);
            throw new RuntimeException("Failed to assign role: " + responseCode);
        }
    }

}
