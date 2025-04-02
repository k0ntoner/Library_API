package repositories;

import org.example.configs.database.JDBCManager;
import org.example.configs.database.TestDatabaseConfig;
import org.example.entities.Book;
import org.example.entities.BookCopy;
import org.example.enums.Status;
import org.example.repositories.BookCopyRepository;
import org.example.repositories.impls.BookCopyRepositoryImpl;
import org.example.repositories.impls.BookRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookCopyRepositoryImplTest {
    private BookRepositoryImpl bookRepository;
    private BookCopyRepository bookCopyRepository;

    @BeforeEach
    public void setUp() throws SQLException {
        TestDatabaseConfig.dropTables();
        TestDatabaseConfig.createTables();
        bookRepository = new BookRepositoryImpl(new JDBCManager(new TestDatabaseConfig()));
        bookCopyRepository = new BookCopyRepositoryImpl(new JDBCManager(new TestDatabaseConfig()), bookRepository);
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

    @Test
    @DisplayName("Should return saved copy of book")
    public void save_ShouldReturnSavedBookCopy() throws SQLException {
        //Given
        Book savedBook = bookRepository.save(buildBook());

        BookCopy bookCopy = buildBookCopy();
        bookCopy.setBook(savedBook);

        //When
        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        //Then
        assertNotNull(savedBookCopy.getId());
        assertEquals(savedBookCopy.getStatus(), bookCopy.getStatus());
        assertEquals(savedBookCopy.getBook().getId(), bookCopy.getBook().getId());
    }

    @Test
    @DisplayName("Should return updated copy of book")
    public void update_ShouldReturnUpdatedBookCopy() throws SQLException {
        //Given
        Book savedBook = bookRepository.save(buildBook());

        BookCopy bookCopy = buildBookCopy();
        bookCopy.setBook(savedBook);

        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        savedBookCopy.setStatus(Status.BORROWED);

        //When
        BookCopy updatedBookCopy = bookCopyRepository.update(savedBookCopy);

        //Then
        assertNotNull(updatedBookCopy.getId());
        assertEquals(updatedBookCopy.getBook().getId(), savedBook.getId());
        assertEquals(Status.BORROWED, updatedBookCopy.getStatus());
    }

    @Test
    @DisplayName("Should return copy of book by id")
    public void findById_ShouldReturnBookCopyById() throws SQLException {
        //Given
        Book savedBook = bookRepository.save(buildBook());

        BookCopy bookCopy = buildBookCopy();
        bookCopy.setBook(savedBook);

        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        //When
        Optional<BookCopy> foundBookCopy = bookCopyRepository.findById(savedBookCopy.getId());

        //Then
        assertTrue(foundBookCopy.isPresent());
        assertEquals(savedBookCopy.getId(), foundBookCopy.get().getId());
        assertEquals(savedBookCopy.getStatus(), foundBookCopy.get().getStatus());
        assertEquals(savedBookCopy.getBook().getId(), foundBookCopy.get().getBook().getId());
    }

    @Test
    @DisplayName("Should delete book and copy of book by CASCADE")
    public void delete_ShouldDeleteBookAndBookCopyByCascade() throws SQLException {
        //Given
        Book savedBook = bookRepository.save(buildBook());

        BookCopy bookCopy = buildBookCopy();
        bookCopy.setBook(savedBook);

        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        Long bookCopyId = savedBookCopy.getId();
        Long bookId = savedBook.getId();

        //When
        bookRepository.delete(savedBook);

        //Then
        assertFalse(bookRepository.findById(bookId).isPresent());
        assertFalse(bookCopyRepository.findById(bookCopyId).isPresent());
    }

    @Test
    @DisplayName("Should delete copy of book")
    public void delete_ShouldDeleteBookCopy() throws SQLException {
        //Given
        Book savedBook = bookRepository.save(buildBook());

        BookCopy bookCopy = buildBookCopy();
        bookCopy.setBook(savedBook);

        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        Long bookCopyId = savedBookCopy.getId();

        //When
        bookCopyRepository.delete(savedBookCopy);

        //Then
        assertFalse(bookCopyRepository.findById(bookCopyId).isPresent());
    }

    @Test
    @DisplayName("Should return all copies")
    public void findAll_ShouldReturnAllBooks() throws SQLException {
        //Given
        Book savedBook = bookRepository.save(buildBook());

        BookCopy firstBookCopy = buildBookCopy();
        firstBookCopy.setBook(savedBook);

        BookCopy firstSavedBookCopy = bookCopyRepository.save(firstBookCopy);

        BookCopy secondBookCopy = buildBookCopy();
        secondBookCopy.setBook(savedBook);

        BookCopy secondSavedBookCopy = bookCopyRepository.save(secondBookCopy);

        //When
        Collection<BookCopy> foundBookCopies = bookCopyRepository.findAll();

        //Then
        assertEquals(2, foundBookCopies.size());
        foundBookCopies.forEach(book -> {
            assertNotNull(book.getId());
            assertNotNull(book.getBook().getId());
            assertNotNull(book.getStatus());
        });
    }

    @Test
    @DisplayName("Should return is copy available")
    public void isBookCopyAvailable_ShouldReturnIsBookCopyAvailable() throws SQLException {
        //Given
        Book savedBook = bookRepository.save(buildBook());

        BookCopy bookCopy = buildBookCopy();
        bookCopy.setBook(savedBook);

        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        //When
        boolean isCopyAvailable = bookCopyRepository.isBookCopyAvailable(savedBookCopy.getId());

        //Then
        assertTrue(isCopyAvailable);
    }
}
