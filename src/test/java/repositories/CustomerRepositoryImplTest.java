package repositories;

import org.example.configs.database.JDBCManager;
import org.example.configs.database.TestDatabaseConfig;
import org.example.entities.Customer;
import org.example.repositories.CustomerRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.impls.CustomerRepositoryImpl;
import org.example.repositories.impls.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CustomerRepositoryImplTest {
    private CustomerRepository customerRepository;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws SQLException {
        TestDatabaseConfig.dropTables();
        TestDatabaseConfig.createTables();
        customerRepository = new CustomerRepositoryImpl(new JDBCManager(new TestDatabaseConfig()));
        userRepository = new UserRepositoryImpl(new JDBCManager(new TestDatabaseConfig()));
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
        Customer customer = buildCustomer();

        //When
        Customer savedCustomer = customerRepository.save(customer);

        //Then
        assertNotNull(savedCustomer.getId());
        assertEquals(customer.getFirstName(), savedCustomer.getFirstName());
        assertEquals(customer.getLastName(), savedCustomer.getLastName());
        assertEquals(customer.getEmail(), savedCustomer.getEmail());
        assertEquals(customer.getPhoneNumber(), savedCustomer.getPhoneNumber());
        assertEquals(customer.getDateOfBirth(), savedCustomer.getDateOfBirth());
    }

    @Test
    @DisplayName("Should return updated customer")
    public void update_ShouldReturnUpdatedCustomer() throws SQLException {
        //Given
        Customer customer = buildCustomer();

        Customer savedCustomer = customerRepository.save(customer);


        String updatedFirstName = "updatedFirstName";
        String updatedLastName = "updatedLastName";
        String updatedEmail = "updatedEmail";
        String updatedPhoneNumber = "+380555555555";
        LocalDate updatedDateOfBirth = LocalDate.now();

        savedCustomer.setFirstName(updatedFirstName);
        savedCustomer.setLastName(updatedLastName);
        savedCustomer.setEmail(updatedEmail);
        savedCustomer.setPhoneNumber(updatedPhoneNumber);
        savedCustomer.setDateOfBirth(updatedDateOfBirth);

        //When
        Customer updatedCustomer = customerRepository.update(savedCustomer);

        //Then
        assertNotNull(updatedCustomer.getId());
        assertEquals(updatedCustomer.getId(), savedCustomer.getId());
        assertEquals(updatedFirstName, updatedCustomer.getFirstName());
        assertEquals(updatedLastName, updatedCustomer.getLastName());
        assertEquals(updatedEmail, updatedCustomer.getEmail());
        assertEquals(updatedPhoneNumber, updatedCustomer.getPhoneNumber());
        assertEquals(updatedDateOfBirth, updatedCustomer.getDateOfBirth());
    }

    @Test
    @DisplayName("Should return customer by id")
    public void findById_ShouldReturnCustomerById() throws SQLException {
        //Given
        Customer customer = buildCustomer();

        Customer savedCustomer = customerRepository.save(customer);

        //When
        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getId());

        //Then
        assertTrue(foundCustomer.isPresent());
        assertEquals(savedCustomer.getId(), foundCustomer.get().getId());
        assertEquals(savedCustomer.getFirstName(), foundCustomer.get().getFirstName());
        assertEquals(savedCustomer.getLastName(), foundCustomer.get().getLastName());
        assertEquals(savedCustomer.getEmail(), foundCustomer.get().getEmail());
        assertEquals(savedCustomer.getPhoneNumber(), foundCustomer.get().getPhoneNumber());
        assertEquals(savedCustomer.getDateOfBirth(), foundCustomer.get().getDateOfBirth());
    }

    @Test
    @DisplayName("Should delete customer")
    public void delete_ShouldDeleteCustomer() throws SQLException {
        //Given
        Customer customer = buildCustomer();

        Customer savedCustomer = customerRepository.save(customer);

        String customerId = savedCustomer.getId();

        //When
        customerRepository.delete(savedCustomer);

        //Then
        assertFalse(customerRepository.findById(customerId).isPresent());
        assertFalse(userRepository.findById(customerId).isPresent());
    }

    @Test
    @DisplayName("Should return all customers")
    public void findAll_ShouldReturnAllBooks() throws SQLException {
        //Given
        Customer customer = buildCustomer();
        Customer firstSavedCustomer = customerRepository.save(customer);
        customer.setId("secondAuth0Id");
        customer.setEmail("secondEmail@example.com");
        customer.setPhoneNumber("secondPassword");
        Customer secondSavedCustomer = customerRepository.save(customer);

        //When
        Collection<Customer> foundCustomers = customerRepository.findAll();

        //Then
        assertEquals(2, foundCustomers.size());
        foundCustomers.forEach(foundCustomer -> {
            assertNotNull(foundCustomer.getId());
            assertNotNull(foundCustomer.getFirstName());
            assertNotNull(foundCustomer.getLastName());
            assertNotNull(foundCustomer.getEmail());
            assertNotNull(foundCustomer.getPhoneNumber());
            assertNotNull(foundCustomer.getDateOfBirth());
        });
    }
}
