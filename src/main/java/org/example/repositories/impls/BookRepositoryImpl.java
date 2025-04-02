package org.example.repositories.impls;

import lombok.extern.slf4j.Slf4j;
import org.example.configs.database.JDBCManager;
import org.example.entities.Book;
import org.example.repositories.BookRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BookRepositoryImpl implements BookRepository {
    private JDBCManager jdbcManager;

    public BookRepositoryImpl(JDBCManager jdbcManager) {
        this.jdbcManager = jdbcManager;
    }


    public Collection<Book> findAll() throws SQLException {
        log.info("Request to get all books from database...");

        String query = "SELECT * FROM books";

        try (Connection connection = jdbcManager.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<Book> books = new ArrayList<>();

            while (resultSet.next()) {
                Book book = Book.builder()
                        .id(resultSet.getLong("id"))
                        .title(resultSet.getString("title"))
                        .description(resultSet.getString("description"))
                        .author(resultSet.getString("author"))
                        .build();
                books.add(book);
            }
            return books;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public Book save(Book entity) throws SQLException {
        log.info("Request to save book with title: {" + entity.getTitle() + "} into database...");

        String query = "INSERT INTO books (title, description, author) VALUES (?, ?, ?) RETURNING id, title, description, author";

        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getDescription());
            statement.setString(3, entity.getAuthor());

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    Book savedEntity = Book.builder()
                            .id(result.getLong("id"))
                            .title(result.getString("title"))
                            .description(result.getString("description"))
                            .author(result.getString("author"))
                            .build();

                    connection.commit();
                    return savedEntity;
                }
                throw new SQLException("Error while saving book with title: " + entity.getTitle());
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
    public Optional<Book> findById(Long id) throws SQLException {
        log.info("Request to find Book with id: {" + id + "} from database...");
        String query = "SELECT * FROM books WHERE id = ?";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Book foundBook = Book.builder()
                        .id(resultSet.getLong("id"))
                        .title(resultSet.getString("title"))
                        .description(resultSet.getString("description"))
                        .author(resultSet.getString("author"))
                        .build();
                return Optional.of(foundBook);
            }
            return Optional.empty();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public Book update(Book entity) throws SQLException {
        log.info("Request to update Book with id: {" + entity.getId() + "} into database...");

        String query = "UPDATE books SET title = ?, description = ?, author = ? WHERE id = ? RETURNING id, title, description, author";
        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getDescription());
            statement.setString(3, entity.getAuthor());
            statement.setLong(4, entity.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Book updatedBook = Book.builder()
                            .id(resultSet.getLong("id"))
                            .title(resultSet.getString("title"))
                            .description(resultSet.getString("description"))
                            .author(resultSet.getString("author"))
                            .build();
                    connection.commit();
                    return updatedBook;
                }
                throw new SQLException("Error while updating book with title: " + entity.getTitle());
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
    public void delete(Book entity) throws SQLException {
        log.info("Request to delete Book with id: {" + entity.getId() + "} from database...");

        String query = "DELETE FROM books WHERE id = ?";

        try (Connection connection = jdbcManager.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setLong(1, entity.getId());
            try {
                statement.execute();
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
    public boolean isCopiesOfBookBorrowed(Long id) throws SQLException {
        log.info("Request to check is copies of book with id: {" + id + "} is borrowed");
        String query = "SELECT * FROM orders " +
                "INNER JOIN book_copies on book_copies.id = orders.copy_id" +
                " WHERE book_copies.book_id = ?";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean isTitleExists(String title) throws SQLException {
        log.info("Request to check is title exists: {" + title + "} ");
        String query = "SELECT * FROM books " +
                " WHERE title = ?";

        try (Connection connection = jdbcManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
