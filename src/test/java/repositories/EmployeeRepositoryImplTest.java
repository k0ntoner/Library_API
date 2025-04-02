package repositories;

import org.example.configs.database.JDBCManager;
import org.example.configs.database.TestDatabaseConfig;
import org.example.entities.Employee;
import org.example.repositories.EmployeeRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.impls.EmployeeRepositoryImpl;
import org.example.repositories.impls.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmployeeRepositoryImplTest {
    private EmployeeRepository employeeRepositoryImpl;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws SQLException {
        TestDatabaseConfig.dropTables();
        TestDatabaseConfig.createTables();
        employeeRepositoryImpl = new EmployeeRepositoryImpl(new JDBCManager(new TestDatabaseConfig()));
        userRepository = new UserRepositoryImpl(new JDBCManager(new TestDatabaseConfig()));
    }

    private Employee buildEmployee() throws SQLException {
        return Employee.builder()
                .id("auth0Id")
                .firstName("firstName")
                .lastName("lastName")
                .email("email@example.com")
                .phoneNumber("+380777777777")
                .salary(new BigDecimal(1000))
                .build();
    }

    @Test
    @DisplayName("Should return saved employee")
    public void save_ShouldReturnSavedEmployee() throws SQLException {
        //Given
        Employee employee = buildEmployee();

        //When
        Employee savedEmployee = employeeRepositoryImpl.save(employee);

        //Then
        assertNotNull(savedEmployee.getId());
        assertEquals(employee.getFirstName(), savedEmployee.getFirstName());
        assertEquals(employee.getLastName(), savedEmployee.getLastName());
        assertEquals(employee.getEmail(), savedEmployee.getEmail());
        assertEquals(employee.getPhoneNumber(), savedEmployee.getPhoneNumber());
        assertTrue(employee.getSalary().compareTo(savedEmployee.getSalary()) == 0);
    }

    @Test
    @DisplayName("Should return updated employee")
    public void update_ShouldReturnUpdatedEmployee() throws SQLException {
        //Given
        Employee employee = buildEmployee();

        Employee savedEmployee = employeeRepositoryImpl.save(employee);


        String updatedFirstName = "updatedFirstName";
        String updatedLastName = "updatedLastName";
        String updatedEmail = "updatedEmail@example.com";
        String updatedPassword = "updatedPassword";
        String updatedPhoneNumber = "+380555555555";
        BigDecimal updatedSalary = new BigDecimal("2000");

        savedEmployee.setFirstName(updatedFirstName);
        savedEmployee.setLastName(updatedLastName);
        savedEmployee.setEmail(updatedEmail);
        savedEmployee.setPhoneNumber(updatedPhoneNumber);
        savedEmployee.setSalary(updatedSalary);

        //When
        Employee updatedEmployee = employeeRepositoryImpl.update(savedEmployee);

        //Then
        assertNotNull(updatedEmployee.getId());
        assertEquals(updatedEmployee.getId(), savedEmployee.getId());
        assertEquals(updatedFirstName, updatedEmployee.getFirstName());
        assertEquals(updatedLastName, updatedEmployee.getLastName());
        assertEquals(updatedEmail, updatedEmployee.getEmail());
        assertEquals(updatedPhoneNumber, updatedEmployee.getPhoneNumber());
        assertTrue(updatedSalary.compareTo(updatedEmployee.getSalary()) == 0);
    }

    @Test
    @DisplayName("Should return employee by id")
    public void findById_ShouldReturnCustomerById() throws SQLException {
        //Given
        Employee employee = buildEmployee();

        Employee savedEmployee = employeeRepositoryImpl.save(employee);

        //When
        Optional<Employee> foundEmployee = employeeRepositoryImpl.findById(savedEmployee.getId());

        //Then
        assertTrue(foundEmployee.isPresent());
        assertEquals(savedEmployee.getId(), foundEmployee.get().getId());
        assertEquals(savedEmployee.getFirstName(), foundEmployee.get().getFirstName());
        assertEquals(savedEmployee.getLastName(), foundEmployee.get().getLastName());
        assertEquals(savedEmployee.getEmail(), foundEmployee.get().getEmail());
        assertEquals(savedEmployee.getPhoneNumber(), foundEmployee.get().getPhoneNumber());
        assertTrue(savedEmployee.getSalary().compareTo(foundEmployee.get().getSalary()) == 0);
    }

    @Test
    @DisplayName("Should delete customer")
    public void delete_ShouldDeleteCustomer() throws SQLException {
        //Given
        Employee employee = buildEmployee();

        Employee savedEmployee = employeeRepositoryImpl.save(employee);

        String employeeId = savedEmployee.getId();

        //When
        employeeRepositoryImpl.delete(savedEmployee);

        //Then
        assertFalse(employeeRepositoryImpl.findById(employeeId).isPresent());
        assertFalse(userRepository.findById(employeeId).isPresent());
    }

    @Test
    @DisplayName("Should return all customers")
    public void findAll_ShouldReturnAllBooks() throws SQLException {
        //Given
        Employee employee = buildEmployee();
        Employee firstSavedEmployee = employeeRepositoryImpl.save(employee);
        employee.setId("secondAuth0Id");
        employee.setEmail("secondEmail@example.com");
        employee.setPhoneNumber("secondPassword");
        Employee secondSavedEmployee = employeeRepositoryImpl.save(employee);

        //When
        Collection<Employee> foundEmployees = employeeRepositoryImpl.findAll();

        //Then
        assertEquals(2, foundEmployees.size());
        foundEmployees.forEach(foundCustomer -> {
            assertNotNull(foundCustomer.getId());
            assertNotNull(foundCustomer.getFirstName());
            assertNotNull(foundCustomer.getLastName());
            assertNotNull(foundCustomer.getEmail());
            assertNotNull(foundCustomer.getPhoneNumber());
            assertNotNull(foundCustomer.getSalary());
        });
    }
}
