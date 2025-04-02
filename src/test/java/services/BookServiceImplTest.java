package services;

import org.example.entities.Book;
import org.example.repositories.BookRepository;
import org.example.services.BookService;
import org.example.services.impls.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookServiceImplTest {

    private BookRepository mockBookRepository;
    private BookService bookService;

    @BeforeEach
    public void setUp() throws SQLException {
        mockBookRepository = Mockito.mock(BookRepository.class);
        bookService = new BookServiceImpl(mockBookRepository);
        when(mockBookRepository.isTitleExists(any())).thenReturn(false);
        when(mockBookRepository.isCopiesOfBookBorrowed(any())).thenReturn(false);
        when(mockBookRepository.findById(any())).thenReturn(Optional.of(buildBook()));
    }

    private Book buildBook() throws SQLException {

        return Book.builder()
                .id(1L)
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

        when(mockBookRepository.save(book)).thenReturn(book);

        //When
        bookService.save(book);

        //Then
        verify(mockBookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("Should return updated book")
    public void update_ShouldReturnUpdatedBook() throws SQLException {
        //Given
        Book book = buildBook();

        when(mockBookRepository.update(book)).thenReturn(book);

        //When
        bookService.update(book);

        //Then
        verify(mockBookRepository, times(1)).update(book);
    }

    @Test
    @DisplayName("Should return book by id")
    public void findById_ShouldReturnBookById() throws SQLException {
        //Given
        Book book = buildBook();

        when(mockBookRepository.findById(any())).thenReturn(Optional.of(book));
        //When
        bookService.findById(book.getId());

        //Then
        verify(mockBookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should delete book")
    public void delete_ShouldDeleteBook() throws SQLException {
        //Given
        Book book = buildBook();


        //When
        bookService.delete(book);

        //Then
        verify(mockBookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Should return all books")
    public void findAll_ShouldReturnAllBooks() throws SQLException {
        //Given
        when(mockBookRepository.findAll()).thenReturn(new ArrayList<>());

        //When
        bookService.findAll();

        //Then
        verify(mockBookRepository, times(1)).findAll();
    }
}
