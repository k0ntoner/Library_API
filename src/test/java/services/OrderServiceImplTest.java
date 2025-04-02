package services;

import org.example.entities.Book;
import org.example.entities.BookCopy;
import org.example.entities.Customer;
import org.example.entities.Order;
import org.example.enums.Status;
import org.example.enums.SubscriptionType;
import org.example.repositories.BookCopyRepository;
import org.example.repositories.OrderRepository;
import org.example.repositories.UserRepository;
import org.example.services.BookCopyService;
import org.example.services.OrderService;
import org.example.services.impls.BookCopyServiceImpl;
import org.example.services.impls.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class OrderServiceImplTest {
    private BookCopyRepository mockBookCopyRepository;
    private UserRepository mockUserRepository;
    private OrderRepository mockOrderRepository;
    private OrderService orderService;

    @BeforeEach
    public void setUp() throws SQLException {
        mockBookCopyRepository = Mockito.mock(BookCopyRepository.class);
        mockUserRepository = Mockito.mock(UserRepository.class);
        mockOrderRepository = Mockito.mock(OrderRepository.class);
        orderService = new OrderServiceImpl(mockOrderRepository, mockBookCopyRepository, mockUserRepository);

        when(mockBookCopyRepository.isBookCopyAvailable(any())).thenReturn(true);
        when(mockBookCopyRepository.findById(any())).thenReturn(Optional.of(buildBookCopy()));
        when(mockUserRepository.findByPhoneNumber(any())).thenReturn(Optional.of(buildCustomer()));
        when(mockUserRepository.findByPhoneNumber(any())).thenReturn(Optional.empty());
        when(mockOrderRepository.findById(any())).thenReturn(Optional.of(buildOrder()));
    }

    private Book buildBook() throws SQLException {

        return Book.builder()
                .id(1L)
                .title("testBook")
                .author("testAuthor")
                .description("testDescription")
                .build();
    }

    private BookCopy buildBookCopy() throws SQLException {

        return BookCopy.builder()
                .id(1L)
                .book(buildBook())
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
                .id(1L)
                .bookCopy(buildBookCopy())
                .user(buildCustomer())
                .subscriptionType(SubscriptionType.SUBSCRIPTION)
                .orderDate(LocalDate.now())
                .expirationDate(LocalDate.now().plusDays(1))
                .status(Status.BORROWED)
                .build();
    }


    @Test
    @DisplayName("Should return saved order")
    public void save_ShouldReturnSavedOrder() throws SQLException {
        //Given
        when(mockOrderRepository.save(any())).thenReturn(buildOrder());

        //When
        orderService.save(buildOrder());

        //Then
        verify(mockOrderRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should return updated order")
    public void update_ShouldReturnUpdatedOrder() throws SQLException {
        //Given
        when(mockOrderRepository.update(any())).thenReturn(buildOrder());

        //When
        orderService.update(buildOrder());

        //Then
        verify(mockOrderRepository, times(1)).update(any());
    }

    @Test
    @DisplayName("Should return order by id")
    public void findById_ShouldReturnOrderById() throws SQLException {
        //Given
        when(mockOrderRepository.findById(any())).thenReturn(Optional.of(buildOrder()));

        //When
        orderService.findById(buildOrder().getId());

        //Then
        verify(mockOrderRepository, times(2)).findById(any());
    }

    @Test
    @DisplayName("Should delete order")
    public void delete_ShouldDeleteOrder() throws SQLException {
        //Given

        //When
        orderService.delete(buildOrder());

        //Then
        verify(mockOrderRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("Should return all orders")
    public void findAll_ShouldReturnAllOrders() throws SQLException {
        //Given
        when(mockOrderRepository.findAll()).thenReturn(new ArrayList<>());

        //When
        orderService.findAll();

        //Then
        verify(mockOrderRepository, times(1)).findAll();
    }
}
