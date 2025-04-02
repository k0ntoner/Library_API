package repositories;

import org.example.configs.database.JDBCManager;
import org.example.configs.database.TestDatabaseConfig;
import org.example.entities.Book;
import org.example.entities.BookCopy;
import org.example.entities.Customer;
import org.example.entities.Order;
import org.example.enums.Status;
import org.example.enums.SubscriptionType;
import org.example.repositories.*;
import org.example.repositories.impls.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderRepositoryImplTest {
    private BookCopyRepository bookCopyRepository;
    private BookRepository bookRepository;
    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() throws SQLException {
        TestDatabaseConfig.dropTables();
        TestDatabaseConfig.createTables();
        customerRepository = new CustomerRepositoryImpl(new JDBCManager(new TestDatabaseConfig()));
        bookRepository = new BookRepositoryImpl(new JDBCManager(new TestDatabaseConfig()));
        bookCopyRepository = new BookCopyRepositoryImpl(new JDBCManager(new TestDatabaseConfig()), bookRepository);
        orderRepository = new OrderRepositoryImpl(new JDBCManager(new TestDatabaseConfig()), new UserRepositoryImpl(new JDBCManager(new TestDatabaseConfig())), bookCopyRepository);
    }

    private Book buildBook() throws SQLException {

        return Book.builder()
                .title("testBook")
                .author("testAuthor")
                .description("testDescription")
                .build();
    }

    private BookCopy buildBookCopy() throws SQLException {

        return BookCopy.builder()
                .status(Status.AVAILABLE)
                .build();
    }

    private Customer buildCustomer() throws SQLException {
        return Customer.builder()
                .id("auth0Id")
                .firstName("firstName")
                .lastName("lastName")
                .email("email@example.com")
                .phoneNumber("+380777777777")
                .dateOfBirth(LocalDate.now())
                .build();
    }

    private Order buildOrder() throws SQLException {
        return Order.builder()
                .subscriptionType(SubscriptionType.SUBSCRIPTION)
                .orderDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusDays(1))
                .status(Status.BORROWED)
                .build();
    }

    private Order registerOrder() throws SQLException {
        Book savedBook = bookRepository.save(buildBook());

        BookCopy bookCopy = buildBookCopy();
        bookCopy.setBook(savedBook);

        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        Customer customer = buildCustomer();

        Customer savedCustomer = customerRepository.save(customer);

        Order order = buildOrder();
        order.setBookCopy(savedBookCopy);
        order.setUser(savedCustomer);

        return orderRepository.save(order);
    }

    @Test
    @DisplayName("Should return saved order")
    public void save_ShouldReturnSavedOrder() throws SQLException {
        //Given
        Book savedBook = bookRepository.save(buildBook());

        BookCopy bookCopy = buildBookCopy();
        bookCopy.setBook(savedBook);

        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        Customer customer = buildCustomer();

        Customer savedCustomer = customerRepository.save(customer);

        Order order = buildOrder();
        order.setBookCopy(savedBookCopy);
        order.setUser(savedCustomer);

        //When
        Order savedOrder = orderRepository.save(order);


        //Then
        assertNotNull(savedOrder.getId());
        assertEquals(savedBookCopy.getId(), savedOrder.getBookCopy().getId());
        assertEquals(savedCustomer.getId(), savedOrder.getUser().getId());
        assertEquals(savedOrder.getOrderDate(), order.getOrderDate());
        assertEquals(savedOrder.getStatus(), order.getStatus());
        assertEquals(savedOrder.getExpirationDate(), order.getExpirationDate());
        assertEquals(savedOrder.getSubscriptionType(), order.getSubscriptionType());
        assertEquals(Status.BORROWED, savedOrder.getBookCopy().getStatus());
    }

    @Test
    @DisplayName("Should return updated order")
    public void update_ShouldReturnUpdatedOrder() throws SQLException {
        //Given
        Order savedOrder = registerOrder();

        SubscriptionType updatedSubscriptionType = SubscriptionType.READING_ROOM;
        LocalDate updatedExpirationDate = LocalDate.now().plusDays(1);
        LocalDate updatedOrderDate = LocalDate.now();
        Status updatedStatus = Status.RETURNED;

        savedOrder.setStatus(updatedStatus);
        savedOrder.setExpirationDate(updatedExpirationDate);
        savedOrder.setOrderDate(updatedOrderDate);
        savedOrder.setSubscriptionType(updatedSubscriptionType);

        //When
        Order updatedOrder = orderRepository.update(savedOrder);

        //Then
        assertNotNull(updatedOrder.getId());
        assertEquals(updatedOrder.getBookCopy().getId(), savedOrder.getBookCopy().getId());
        assertEquals(updatedOrder.getUser().getId(), savedOrder.getUser().getId());
        assertEquals(updatedOrder.getOrderDate(), updatedOrderDate);
        assertEquals(updatedOrder.getStatus(), updatedStatus);
        assertEquals(updatedOrder.getExpirationDate(), updatedExpirationDate);
        assertEquals(updatedOrder.getSubscriptionType(), updatedSubscriptionType);
        assertEquals(updatedOrder.getBookCopy().getStatus(), Status.AVAILABLE);
    }

    @Test
    @DisplayName("Should return order by id")
    public void findById_ShouldReturnOrderById() throws SQLException {
        //Given
        Order savedOrder = registerOrder();

        //When
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        //Then
        assertNotNull(foundOrder.get().getId());
        assertEquals(foundOrder.get().getId(), savedOrder.getBookCopy().getId());
        assertEquals(foundOrder.get().getUser().getId(), savedOrder.getUser().getId());
        assertEquals(foundOrder.get().getOrderDate(), savedOrder.getOrderDate());
        assertEquals(foundOrder.get().getStatus(), savedOrder.getStatus());
        assertEquals(foundOrder.get().getExpirationDate(), savedOrder.getExpirationDate());
        assertEquals(foundOrder.get().getSubscriptionType(), savedOrder.getSubscriptionType());
    }

    @Test
    @DisplayName("Should delete order")
    public void delete_ShouldDeleteBookCopy() throws SQLException {
        //Given
        Order savedOrder = registerOrder();
        Long orderId = savedOrder.getId();
        //When
        orderRepository.delete(savedOrder);

        //Then
        assertFalse(orderRepository.findById(orderId).isPresent());
    }

    @Test
    @DisplayName("Should return all books")
    public void findAll_ShouldReturnAllBooks() throws SQLException {
        //Given
        Order savedOrder = registerOrder();

        //When
        Collection<Order> orders = orderRepository.findAll();

        //Then
        assertEquals(1, orders.size());
        orders.forEach(order -> {
            assertNotNull(order.getId());
            assertEquals(order.getBookCopy().getId(), savedOrder.getBookCopy().getId());
            assertEquals(order.getUser().getId(), savedOrder.getUser().getId());
            assertEquals(order.getOrderDate(), savedOrder.getOrderDate());
            assertEquals(order.getStatus(), savedOrder.getStatus());
            assertEquals(order.getExpirationDate(), savedOrder.getExpirationDate());
            assertEquals(order.getSubscriptionType(), savedOrder.getSubscriptionType());
        });
    }


}
