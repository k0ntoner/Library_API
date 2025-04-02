package org.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.configs.DIContainer;
import org.example.controllers.CustomerController;
import org.example.controllers.EmployeeController;
import org.example.controllers.UserController;
import org.example.entities.Customer;
import org.example.entities.Employee;
import org.example.entities.User;
import org.example.utils.JwtUtil;
import org.example.utils.ThymeleafUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;


@WebServlet("/users/*")
@Slf4j
public class UserServlet extends HttpServlet {
    private UserController userController;
    private CustomerController customerController;
    private EmployeeController employeeController;

    @Override
    public void init() throws ServletException {
        DIContainer diContainer = (DIContainer) getServletContext().getAttribute(DIContainer.class.getName());
        userController = diContainer.getSingleton(UserController.class);
        customerController = diContainer.getSingleton(CustomerController.class);
        employeeController = diContainer.getSingleton(EmployeeController.class);
        log.info("UserServlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String passInfo = request.getPathInfo();

        try {
            if (passInfo == null || "/".equals(passInfo)) {
                ThymeleafUtil.render(request, response, getServletContext(), "users/users", null);

            } else if (passInfo.startsWith("/user")) {
                Optional<String> userId = JwtUtil.extractAuthId(request);

                if (userId.isPresent()) {
                    User user = userController.getUser(userId.get());

                    Map<String, Object> model = new HashMap<>();
                    model.put("user", user);

                    if (user instanceof Employee) {
                        model.put("isEmployee", true);
                    } else if (user instanceof Customer) {
                        model.put("isCustomer", true);
                    }

                    ThymeleafUtil.render(request, response, getServletContext(), "users/user-dashboard.html", model);
                } else {
                    response.sendRedirect(request.getContextPath() + "/auth/login");
                }

            } else if (passInfo.startsWith("/update")) {
                Optional<String> userId = JwtUtil.extractAuthId(request);

                if (userId.isPresent()) {
                    User user = userController.getUser(userId.get());

                    Map<String, Object> model = new HashMap<>();
                    model.put("user", user);

                    if (user instanceof Employee) {
                        model.put("isEmployee", true);
                    } else if (user instanceof Customer) {
                        model.put("isCustomer", true);
                    }

                    ThymeleafUtil.render(request, response, getServletContext(), "users/user-update.html", model);
                } else {
                    response.sendRedirect(request.getContextPath() + "/auth/login");
                }
            }

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("_method").equals("put")) {
            doPut(request, response);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            User updatedUser;
            if (request.getParameter("isEmployee").equals("true")) {
                updatedUser = employeeController.updateEmployee(mapToEmployee(request));
            } else if (request.getParameter("isCustomer").equals("true")) {
                updatedUser = customerController.updateCustomer(mapToCustomer(request));
            } else {
                throw new NoSuchElementException("Such type of user is not exists");
            }
            response.sendRedirect(request.getContextPath() + "/users/user");
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.sendError(500, e.getMessage());
        }
    }

    private Employee mapToEmployee(HttpServletRequest request) {
        Employee employee = Employee.builder()
                .firstName(request.getParameter("firstName"))
                .lastName(request.getParameter("lastName"))
                .email(request.getParameter("email"))
                .phoneNumber(request.getParameter("phoneNumber"))
                .salary(BigDecimal.valueOf(Double.parseDouble(request.getParameter("salary"))))
                .build();
        if (request.getParameter("id") != null) {
            employee.setId(request.getParameter("id"));
        }
        return employee;
    }

    private Customer mapToCustomer(HttpServletRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.getParameter("firstName"))
                .lastName(request.getParameter("lastName"))
                .email(request.getParameter("email"))
                .phoneNumber(request.getParameter("phoneNumber"))
                .dateOfBirth(LocalDate.parse(request.getParameter("dateOfBirth")))
                .build();
        if (request.getParameter("id") != null) {
            customer.setId(request.getParameter("id"));
        }
        return customer;

    }
}
