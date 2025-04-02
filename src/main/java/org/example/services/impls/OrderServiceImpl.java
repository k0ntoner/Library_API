package org.example.services.impls;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.Order;
import org.example.enums.Status;
import org.example.repositories.BookCopyRepository;
import org.example.repositories.OrderRepository;
import org.example.repositories.UserRepository;
import org.example.services.OrderService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, BookCopyRepository bookCopyRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Collection<Order> findByEmail(String email) throws SQLException {
        log.info("Request to find Orders by email {}", email);
        return orderRepository.findByUserId(email);
    }


    private Order checkOrderExpiration(Long id) throws SQLException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            if (!order.get().getExpirationDate().isAfter(LocalDate.now())) {
                log.info("Order with id:{}, has expired (expired date: " + order.get().getExpirationDate() + ", today: " + LocalDate.now() + "), its status changed to Overdue", id);
                order.get().setStatus(Status.OVERDUE);
                return orderRepository.update(order.get());
            }
            return order.get();
        }
        throw new SQLException("Order with id: {" + id + "} not found");

    }

    @Override
    public Order save(Order entity) throws SQLException {
        log.info("Request to save Order {}", entity);
        if (bookCopyRepository.isBookCopyAvailable(entity.getBookCopy().getId())) {
            if (!userRepository.isUserHasOverdue(entity.getUser().getId())) {
                return orderRepository.save(entity);
            }
            throw new IllegalArgumentException("User with id: {" + entity.getUser().getId() + "}, has overdue book copy");
        }
        throw new IllegalArgumentException("Copy of book with id: {" + entity.getBookCopy().getId() + "}, has already borrowed");

    }

    @Override
    public Order findById(Long id) throws SQLException {
        log.info("Request to find Order by id {}", id);
        checkOrderExpiration(id);
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return order.get();
        }
        throw new SQLException("Order with id: {" + id + "} not found");
    }

    @Override
    public Order update(Order entity) throws SQLException {
        log.info("Request to update Order {}", entity);
        Order foundOrder = findById(entity.getId());
        if (foundOrder.getUser().getId().equals(entity.getUser().getId())) {
            if (foundOrder.getBookCopy().getId().equals(entity.getBookCopy().getId())) {
                return orderRepository.update(entity);
            }
            throw new IllegalArgumentException("While updating order is promised to change book copy");
        }
        throw new IllegalArgumentException("While updating order is promised to change user");
    }

    @Override
    public void delete(Order entity) throws SQLException {
        log.info("Request to delete Order {}", entity);
        if(!entity.getStatus().equals(Status.RETURNED)) {
            throw new IllegalArgumentException("Book copy in Order with id: {" + entity.getId() + "} has not been returned");
        }
        orderRepository.delete(entity);
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        delete(findById(id));
    }

    @Override
    public Collection<Order> findAll() throws SQLException {
        log.info("Request to find all Orders");
        Collection<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            order.setStatus(checkOrderExpiration(order.getId()).getStatus());
        }
        return orders;
    }


}
