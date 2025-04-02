package repositories;


import org.example.configs.database.TestDatabaseConfig;
import org.example.configs.database.JDBCManager;
import org.example.entities.Book;
import org.example.repositories.BookRepository;
import org.example.repositories.impls.BookRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BookRepositoryImplTest {

    private BookRepository bookRepository;

    @BeforeEach
    public void setUp() throws SQLException {
        TestDatabaseConfig.dropTables();
        TestDatabaseConfig.createTables();
        bookRepository = new BookRepositoryImpl(new JDBCManager(new TestDatabaseConfig()));
    }

    private Book buildBook() throws SQLException {

        return Book.builder()
                .title("testBook")
                .author("testAuthor")
                .description("testDescription")
                .build();
    }

    @Test
    @DisplayName("Should return saved book")
    public void save_ShouldReturnSavedBook() throws SQLException {
        //Given
        Book book = buildBook();

        //When
        Book savedBook = bookRepository.save(book);

        //Then
        assertNotNull(savedBook.getId());
        assertEquals(savedBook.getTitle(), book.getTitle());
        assertEquals(savedBook.getAuthor(), book.getAuthor());
        assertEquals(savedBook.getDescription(), book.getDescription());
    }

    @Test
    @DisplayName("Should return updated book")
    public void update_ShouldReturnUpdatedBook() throws SQLException {
        //Given
        Book savedBook = bookRepository.save(buildBook());

        String updatedTitle = "updatedTitle";
        String updatedAuthor = "updatedAuthor";
        String updatedDescription = "updatedDescription";

        savedBook.setTitle(updatedTitle);
        savedBook.setAuthor(updatedAuthor);
        savedBook.setDescription(updatedDescription);

        //When
        Book updatedBook = bookRepository.update(savedBook);

        //Then
        assertNotNull(updatedBook.getId());
        assertEquals(updatedTitle, updatedBook.getTitle());
        assertEquals(updatedAuthor, updatedBook.getAuthor());
        assertEquals(updatedDescription, updatedBook.getDescription());
    }

    @Test
    @DisplayName("Should return book by id")
    public void findById_ShouldReturnBookById() throws SQLException {
        //Given
        Book savedBook = bookRepository.save(buildBook());

        //When
        Optional<Book> foundBook = bookRepository.findById(savedBook.getId());

        //Then
        assertTrue(foundBook.isPresent());
        assertEquals(savedBook.getId(), foundBook.get().getId());
        assertEquals(savedBook.getTitle(), foundBook.get().getTitle());
        assertEquals(savedBook.getAuthor(), foundBook.get().getAuthor());
        assertEquals(savedBook.getDescription(), foundBook.get().getDescription());
    }

    @Test
    @DisplayName("Should delete book")
    public void delete_ShouldDeleteBook() throws SQLException {
        //Given
        Book savedBook = bookRepository.save(buildBook());
        Long id = savedBook.getId();

        //When
        bookRepository.delete(savedBook);

        //Then
        assertFalse(bookRepository.findById(id).isPresent());
    }

    @Test
    @DisplayName("Should return all books")
    public void findAll_ShouldReturnAllBooks() throws SQLException {
        //Given
        Book book = buildBook();
        Book fisrtSavedBook = bookRepository.save(book);

        book.setTitle("secondTitle");
        Book secondSavedBook = bookRepository.save(book);

        //When
        Collection<Book> foundBooks = bookRepository.findAll();

        //Then
        assertEquals(2, foundBooks.size());
        foundBooks.forEach(foundBook -> {
            assertNotNull(foundBook.getId());
            assertNotNull(foundBook.getTitle());
            assertNotNull(foundBook.getAuthor());
            assertNotNull(foundBook.getDescription());
        });
    }

    @Test
    @DisplayName("Should return is title exists")
    public void isTitleExists_ShouldReturnIsTitleExists() throws SQLException {
        //Given
        Book book = buildBook();
        Book fisrtSavedBook = bookRepository.save(book);

        //When
        boolean exists = bookRepository.isTitleExists(fisrtSavedBook.getTitle());

        //Then
        assertTrue(exists);
    }
}
