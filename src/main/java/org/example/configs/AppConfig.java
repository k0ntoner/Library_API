package org.example.configs;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.database.JDBCManager;
import org.example.configs.database.PostgresDatabaseConfig;
import org.example.controllers.*;
import org.example.repositories.*;
import org.example.repositories.impls.*;
import org.example.services.*;
import org.example.services.impls.*;

@Slf4j
public class AppConfig {
    public static void configure(DIContainer container) {
        //JDBCManager
        container.registerSingleton(PostgresDatabaseConfig.class, postgresDatabaseConfig());
        container.registerSingleton(JDBCManager.class, jdbcManager(container));

        //Repository's singletons
        container.registerSingleton(BookRepository.class, bookRepositoryImpl(container));
        container.registerSingleton(UserRepository.class, userRepositoryImpl(container));
        container.registerSingleton(CustomerRepository.class, customerRepositoryImpl(container));
        container.registerSingleton(EmployeeRepository.class, employeeRepositoryImpl(container));
        container.registerSingleton(BookCopyRepository.class, bookCopyRepositoryImpl(container));
        container.registerSingleton(OrderRepository.class, orderRepositoryImpl(container));

        //Service's singletons
        container.registerSingleton(BookService.class, bookServiceImpl(container));
        container.registerSingleton(BookCopyService.class, bookCopyServiceImpl(container));
        container.registerSingleton(OrderService.class, orderServiceImpl(container));
        container.registerSingleton(CustomerService.class, customerServiceImpl(container));
        container.registerSingleton(EmployeeService.class, employeeServiceImpl(container));
        container.registerSingleton(UserService.class, userServiceImpl(container));

        //Controller's singletons
        container.registerSingleton(OrderController.class, orderController(container));
        container.registerSingleton(CustomerController.class, customerController(container));
        container.registerSingleton(EmployeeController.class, employeeController(container));
        container.registerSingleton(BookCopyController.class, bookCopyController(container));
        container.registerSingleton(BookController.class, bookController(container));
        container.registerSingleton(UserController.class, userController(container));

        log.info("Application's singletons has been configured successfully");
    }

    // Books
    private static BookController bookController(DIContainer container) {
        try {
            return new BookController(container.getSingleton(BookService.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static BookService bookServiceImpl(DIContainer container) {
        try {
            return new BookServiceImpl(container.getSingleton(BookRepository.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static BookRepository bookRepositoryImpl(DIContainer container) {
        try {
            return new BookRepositoryImpl(container.getSingleton(JDBCManager.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // Users
    private static UserService userServiceImpl(DIContainer container) {
        try {
            return new UserServiceImpl(container.getSingleton(UserRepository.class), container.getSingleton(OrderRepository.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static UserRepository userRepositoryImpl(DIContainer container) {
        try {
            return new UserRepositoryImpl(container.getSingleton(JDBCManager.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static UserController userController(DIContainer container) {
        try {
            return new UserController(container.getSingleton(UserService.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // Database
    private static PostgresDatabaseConfig postgresDatabaseConfig() {
        try {
            return new PostgresDatabaseConfig();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static JDBCManager jdbcManager(DIContainer container) {
        try {
            return new JDBCManager(container.getSingleton(PostgresDatabaseConfig.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // Customers
    private static CustomerRepository customerRepositoryImpl(DIContainer container) {
        try {
            return new CustomerRepositoryImpl(container.getSingleton(JDBCManager.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static CustomerService customerServiceImpl(DIContainer container) {
        try {
            return new CustomerServiceImpl(container.getSingleton(CustomerRepository.class), container.getSingleton(UserRepository.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static CustomerController customerController(DIContainer container) {
        try {
            return new CustomerController(container.getSingleton(CustomerService.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // Employees
    private static EmployeeRepository employeeRepositoryImpl(DIContainer container) {
        try {
            return new EmployeeRepositoryImpl(container.getSingleton(JDBCManager.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static EmployeeService employeeServiceImpl(DIContainer container) {
        try {
            return new EmployeeServiceImpl(container.getSingleton(EmployeeRepository.class), container.getSingleton(UserRepository.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static EmployeeController employeeController(DIContainer container) {
        try {
            return new EmployeeController(container.getSingleton(EmployeeService.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // BookCopies
    private static BookCopyRepository bookCopyRepositoryImpl(DIContainer container) {
        try {
            return new BookCopyRepositoryImpl(container.getSingleton(JDBCManager.class), container.getSingleton(BookRepository.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static BookCopyService bookCopyServiceImpl(DIContainer container) {
        try {
            return new BookCopyServiceImpl(container.getSingleton(BookCopyRepository.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static BookCopyController bookCopyController(DIContainer container) {
        try {
            return new BookCopyController(container.getSingleton(BookCopyService.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // Orders
    private static OrderRepository orderRepositoryImpl(DIContainer container) {
        try {
            return new OrderRepositoryImpl(container.getSingleton(JDBCManager.class), container.getSingleton(UserRepository.class), container.getSingleton(BookCopyRepository.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static OrderService orderServiceImpl(DIContainer container) {
        try {
            return new OrderServiceImpl(container.getSingleton(OrderRepository.class), container.getSingleton(BookCopyRepository.class), container.getSingleton(UserRepository.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static OrderController orderController(DIContainer container) {
        try {
            return new OrderController(container.getSingleton(OrderService.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
