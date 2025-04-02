package org.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.configs.DIContainer;
import org.example.controllers.BookCopyController;
import org.example.controllers.OrderController;
import org.example.controllers.UserController;
import org.example.entities.BookCopy;
import org.example.entities.Order;
import org.example.entities.User;
import org.example.enums.Status;
import org.example.enums.SubscriptionType;
import org.example.utils.ThymeleafUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/orders/*")
@Slf4j
public class OrderServlet extends HttpServlet {
    private BookCopyController bookCopyController;
    private UserController userController;
    private OrderController orderController;

    @Override
    public void init() throws ServletException {
        DIContainer container = (DIContainer) getServletContext().getAttribute(DIContainer.class.getName());
        bookCopyController = container.getSingleton(BookCopyController.class);
        userController = container.getSingleton(UserController.class);
        orderController = container.getSingleton(OrderController.class);
        log.info("OrderServlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String passInfo = request.getPathInfo();

        try {
            if (passInfo == null || "/".equals(passInfo)) {
                Collection<Order> orders = orderController.getOrders();

                Map<String, Object> model = new HashMap<>();
                model.put("orders", orders);

                ThymeleafUtil.render(request, response, getServletContext(), "orders/orders", model);
            } else if ("/create".equals(passInfo)) {
                Collection<User> users = userController.getUsers();
                Collection<BookCopy> copies = bookCopyController.getBookCopies();
                Map<String, Object> model = new HashMap<>();
                model.put("users", users);
                model.put("copies", copies);

                ThymeleafUtil.render(request, response, getServletContext(), "orders/order-create", model);
            } else if (passInfo.startsWith("/update/")) {
                Order order = orderController.getOrderById(Long.parseLong(passInfo.substring(8)));

                Map<String, Object> model = new HashMap<>();
                model.put("order", order);

                ThymeleafUtil.render(request, response, getServletContext(), "orders/order-update", model);
            } else if (passInfo.startsWith("/delete/")) {
                Order order = orderController.getOrderById(Long.parseLong(passInfo.substring(8)));

                Map<String, Object> model = new HashMap<>();
                model.put("order", order);

                ThymeleafUtil.render(request, response, getServletContext(), "orders/order-delete", model);
            } else {
                Long id = Long.parseLong(passInfo.substring(1));

                Order order = orderController.getOrderById(id);

                Map<String, Object> model = new HashMap<>();
                model.put("order", order);
                ThymeleafUtil.render(request, response, getServletContext(), "orders/order", model);
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
        String methodOverride = request.getParameter("_method");
        if ("PUT".equalsIgnoreCase(methodOverride)) {
            doPut(request, response);
            return;
        } else if ("DELETE".equalsIgnoreCase(methodOverride)) {
            doDelete(request, response);
            return;
        }

        String passInfo = request.getPathInfo();

        try {
            Order order = mapToOrder(request);
            order = orderController.createOrder(order);
            response.sendRedirect(request.getContextPath() + "/orders/" + order.getId());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String passInfo = request.getPathInfo();

        try {
            Order order = mapToOrder(request);
            order = orderController.updateOrder(order);
            response.sendRedirect(request.getContextPath() + "/orders/" + order.getId());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String passInfo = request.getPathInfo();

        try {
            Long id = Long.parseLong(request.getParameter("id"));
            orderController.deleteOrderById(id);
            response.sendRedirect(request.getContextPath() + "/orders");
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            response.sendError(400, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.sendError(500, e.getMessage());
        }
    }

    private Order mapToOrder(HttpServletRequest request) throws SQLException, IOException {
        Order order = Order.builder()
                .subscriptionType(SubscriptionType.valueOf(request.getParameter("subscriptionType")))
                .orderDate(LocalDate.parse(request.getParameter("orderDate")))
                .expirationDate(LocalDate.parse(request.getParameter("expirationDate")))
                .status(Status.valueOf(request.getParameter("status")))
                .build();


        if (request.getParameter("id") != null) {
            order.setId(Long.parseLong(request.getParameter("id")));
        }
        order.setBookCopy(bookCopyController.getBookCopyById(Long.parseLong(request.getParameter("copyId"))));
        order.setUser(userController.getUser(request.getParameter("userId")));
        return order;
    }
}
