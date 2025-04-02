package org.example.repositories.impls;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.database.JDBCManager;
import org.example.entities.Book;
import org.example.entities.BookCopy;
import org.example.enums.Status;
import org.example.repositories.BookCopyRepository;
import org.example.repositories.BookRepository;

import java.sql.*;
import java.util.*;

@Slf4j
public class BookCopyRepositoryImpl implements BookCopyRepository {
    private JDBCManager jdbcManager;

    private BookRepository bookRepository;

    public BookCopyRepositoryImpl(JDBCManager jdbcManager, BookRepository bookRepository) {
        this.jdbcManager = jdbcManager;
        this.bookRepository = bookRepository;
    }

    @Override
    public Optional<BookCopy> findById(Long id) throws SQLException {
        log.info("Request to find BookCopy with id: {} from database", id);

        String query = "SELECT * FROM book_copies " +
                "INNER JOIN books on book_id = books.id " +
                " WHERE book_copies.id = ?";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                BookCopy bookCopy = BookCopy.builder()
                        .id(resultSet.getLong("id"))
                        .status(Status.valueOf(resultSet.getString("status")))
                        .build();

                Optional<Book> foundBook = bookRepository.findById(resultSet.getLong("book_id"));
                foundBook.ifPresentOrElse(
                        bookCopy::setBook,
                        () -> {
                            throw new NoSuchElementException("Book with id: " + bookCopy.getId() + " not found");
                        }
                );
                return Optional.of(bookCopy);
            }
            return Optional.empty();

        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new SQLException(e);
        }

    }

    @Override
    public Collection<BookCopy> findByEmail(String email) throws SQLException {
        log.info("Request to find Book Copies by username: {}", email);

        String query = "SELECT * FROM book_copies " +
                "INNER JOIN orders ON orders.copy_id = id " +
                "INNER JOIN users ON orders.user_id = users.id " +
                "WHERE users.email = ? ";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<BookCopy> bookCopies = new ArrayList<>();
            while (resultSet.next()) {
                BookCopy bookCopy = BookCopy.builder()
                        .id(resultSet.getLong("id"))
                        .status(Status.valueOf(resultSet.getString("status").toUpperCase()))
                        .build();

                Optional<Book> foundBook = bookRepository.findById(resultSet.getLong("book_id"));
                foundBook.ifPresentOrElse(
                        bookCopy::setBook,
                        () -> {
                            throw new NoSuchElementException("Book with id: " + bookCopy.getId() + " not found");
                        }
                );

                bookCopies.add(bookCopy);
            }
            return bookCopies;
        } catch (SQLException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Collection<BookCopy> findByBookId(Long bookId) throws SQLException {
        log.info("Request to find Book Copies by book id: {}", bookId);

        String query = "SELECT * FROM book_copies " +
                "Where book_id = ? ";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, bookId);

            ResultSet resultSet = preparedStatement.executeQuery();

            Collection<BookCopy> bookCopies = new ArrayList<>();
            while (resultSet.next()) {
                BookCopy bookCopy = BookCopy.builder()
                        .id(resultSet.getLong("id"))
                        .status(Status.valueOf(resultSet.getString("status").toUpperCase()))
                        .build();

                Optional<Book> foundBook = bookRepository.findById(resultSet.getLong("book_id"));
                foundBook.ifPresentOrElse(
                        bookCopy::setBook,
                        () -> {
                            throw new NoSuchElementException("Book with id: " + bookCopy.getId() + " not found");
                        }
                );

                bookCopies.add(bookCopy);
            }
            return bookCopies;
        } catch (SQLException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean isBookCopyAvailable(Long id) throws SQLException {
        log.info("Request to check is book copy is available orders by id: {}", id);

        String query = "SELECT * FROM book_copies " +
                "WHERE id = ? and status = ? ";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, Status.AVAILABLE.toString().toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException | NoSuchElementException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BookCopy save(BookCopy entity) throws SQLException {
        log.info("Request to save BookCopy: {} into database", entity);

        String query = "INSERT INTO book_copies (book_id, status) VALUES (?, ?) RETURNING id, book_id, status;";


        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setLong(1, entity.getBook().getId());
            preparedStatement.setString(2, entity.getStatus().toString().toUpperCase());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    BookCopy bookCopy = BookCopy.builder()
                            .id(resultSet.getLong("id"))
                            .status(Status.valueOf(resultSet.getString("status").toUpperCase()))
                            .build();

                    Optional<Book> foundBook = bookRepository.findById(resultSet.getLong("book_id"));
                    foundBook.ifPresentOrElse(
                            bookCopy::setBook,
                            () -> {
                                throw new NoSuchElementException("Book with id: " + bookCopy.getId() + " not found");
                            }
                    );
                    connection.commit();
                    return bookCopy;
                }
                throw new SQLException("Error while saving BookCopy with id: " + entity.getId());
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public BookCopy update(BookCopy entity) throws SQLException {
        log.info("Request to update BookCopy: {} into database", entity);

        String query = "UPDATE book_copies SET book_id = ?, status = ? WHERE id = ? RETURNING id, book_id, status;";


        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setLong(1, entity.getBook().getId());
            preparedStatement.setString(2, entity.getStatus().toString().toUpperCase());
            preparedStatement.setLong(3, entity.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    BookCopy bookCopy = BookCopy.builder()
                            .id(resultSet.getLong("id"))
                            .status(Status.valueOf(resultSet.getString("status").toUpperCase()))
                            .build();

                    Optional<Book> foundBook = bookRepository.findById(resultSet.getLong("book_id"));
                    foundBook.ifPresentOrElse(
                            bookCopy::setBook,
                            () -> {
                                throw new NoSuchElementException("Book with id: " + bookCopy.getId() + " not found");
                            }
                    );
                    connection.commit();
                    return bookCopy;
                }
                throw new SQLException("Error while saving BookCopy with id: " + entity.getId());
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void delete(BookCopy entity) throws SQLException {
        log.info("Request to delete BookCopy: {} into database", entity);

        String query = "DELETE FROM book_copies WHERE id = ?";


        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setLong(1, entity.getId());

            try {
                preparedStatement.execute();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Collection<BookCopy> findAll() throws SQLException {
        log.info("Request to get all book's copies from database...");

        String query = "SELECT * FROM book_copies";

        try (Connection connection = jdbcManager.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            Collection<BookCopy> bookCopies = new ArrayList<>();

            while (resultSet.next()) {
                BookCopy bookCopy = BookCopy.builder()
                        .id(resultSet.getLong("id"))
                        .status(Status.valueOf(resultSet.getString("status").toUpperCase()))
                        .build();

                Optional<Book> foundBook = bookRepository.findById(resultSet.getLong("book_id"));
                foundBook.ifPresentOrElse(
                        bookCopy::setBook,
                        () -> {
                            throw new NoSuchElementException("Book with id: " + bookCopy.getId() + " not found");
                        }
                );

                bookCopies.add(bookCopy);
            }
            return bookCopies;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
