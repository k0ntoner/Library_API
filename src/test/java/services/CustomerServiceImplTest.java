package services;

import org.example.entities.Book;
import org.example.entities.BookCopy;
import org.example.entities.Customer;
import org.example.enums.Status;
import org.example.repositories.BookCopyRepository;
import org.example.repositories.BookRepository;
import org.example.repositories.CustomerRepository;
import org.example.repositories.UserRepository;
import org.example.services.BookCopyService;
import org.example.services.BookService;
import org.example.services.CustomerService;
import org.example.services.impls.BookCopyServiceImpl;
import org.example.services.impls.BookServiceImpl;
import org.example.services.impls.CustomerServiceImpl;
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

public class CustomerServiceImplTest {
    private UserRepository mockUserRepository;
    private CustomerRepository mockCustomerRepository;
    private CustomerService customerService;

    @BeforeEach
    public void setUp() throws SQLException {
        mockUserRepository = mock(UserRepository.class);
        mockCustomerRepository = mock(CustomerRepository.class);
        customerService = new CustomerServiceImpl(mockCustomerRepository, mockUserRepository);

        when(mockUserRepository.findByPhoneNumber(any())).thenReturn(Optional.of(buildCustomer()));
        when(mockCustomerRepository.findById(any())).thenReturn(Optional.of(buildCustomer()));
        when(mockUserRepository.findByPhoneNumber(any())).thenReturn(Optional.empty());
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


    @Test
    @DisplayName("Should return saved customer")
    public void save_ShouldReturnSavedCustomer() throws SQLException {
        //Given
        when(mockCustomerRepository.save(any())).thenReturn(buildCustomer());

        //When
        customerService.save(buildCustomer());

        //Then
        verify(mockCustomerRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should return updated customer")
    public void update_ShouldReturnUpdatedCustomer() throws SQLException {
        //Given
        when(mockCustomerRepository.update(any())).thenReturn(buildCustomer());

        //When
        customerService.update(buildCustomer());

        //Then
        verify(mockCustomerRepository, times(1)).update(any());
    }

    @Test
    @DisplayName("Should return customer by id")
    public void findById_ShouldReturnCustomerById() throws SQLException {
        //Given
        when(mockCustomerRepository.findById(any())).thenReturn(Optional.of(buildCustomer()));
        //When
        customerService.findById(buildCustomer().getId());

        //Then
        verify(mockCustomerRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Should delete customer")
    public void delete_ShouldDeleteCustomer() throws SQLException {
        //Given

        //When
        customerService.delete(buildCustomer());

        //Then
        verify(mockCustomerRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("Should return all customers")
    public void findAll_ShouldReturnAllCustomers() throws SQLException {
        //Given
        when(mockCustomerRepository.findAll()).thenReturn(new ArrayList<>());

        //When
        customerService.findAll();

        //Then
        verify(mockCustomerRepository, times(1)).findAll();
    }
}
