package services;

import org.example.entities.Customer;
import org.example.entities.Employee;
import org.example.repositories.EmployeeRepository;
import org.example.repositories.UserRepository;
import org.example.services.EmployeeService;
import org.example.services.impls.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmployeeServiceImplTest {
    private UserRepository mockUserRepository;
    private EmployeeRepository mockEmployeeRepository;
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() throws SQLException {
        mockUserRepository = mock(UserRepository.class);
        mockEmployeeRepository = mock(EmployeeRepository.class);
        employeeService = new EmployeeServiceImpl(mockEmployeeRepository, mockUserRepository);

        when(mockUserRepository.findByPhoneNumber(any())).thenReturn(Optional.of(buildEmployee()));
        when(mockEmployeeRepository.findById(any())).thenReturn(Optional.of(buildEmployee()));
        when(mockUserRepository.findByPhoneNumber(any())).thenReturn(Optional.empty());
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
        when(mockEmployeeRepository.save(any())).thenReturn(buildEmployee());

        //When
        employeeService.save(buildEmployee());

        //Then
        verify(mockEmployeeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should return updated employee")
    public void update_ShouldReturnUpdatedEmployee() throws SQLException {
        //Given
        when(mockEmployeeRepository.update(any())).thenReturn(buildEmployee());

        //When
        employeeService.update(buildEmployee());

        //Then
        verify(mockEmployeeRepository, times(1)).update(any());
    }

    @Test
    @DisplayName("Should return employee by id")
    public void findById_ShouldReturnEmployeeById() throws SQLException {
        //Given
        when(mockEmployeeRepository.findById(any())).thenReturn(Optional.of(buildEmployee()));
        //When
        employeeService.findById(buildEmployee().getId());

        //Then
        verify(mockEmployeeRepository, times(1)).findById(any());
    }

    @Test
    @DisplayName("Should delete employee")
    public void delete_ShouldDeleteEmployee() throws SQLException {
        //Given

        //When
        employeeService.delete(buildEmployee());

        //Then
        verify(mockEmployeeRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("Should return all employees")
    public void findAll_ShouldReturnAllEmployees() throws SQLException {
        //Given
        when(mockEmployeeRepository.findAll()).thenReturn(new ArrayList<>());

        //When
        employeeService.findAll();

        //Then
        verify(mockEmployeeRepository, times(1)).findAll();
    }
}
