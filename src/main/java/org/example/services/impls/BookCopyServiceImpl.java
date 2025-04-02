package org.example.services.impls;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.BookCopy;
import org.example.repositories.BookCopyRepository;
import org.example.services.BookCopyService;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Slf4j
public class BookCopyServiceImpl implements BookCopyService {
    private final BookCopyRepository bookCopyRepository;

    public BookCopyServiceImpl(BookCopyRepository bookCopyRepository) {
        this.bookCopyRepository = bookCopyRepository;
    }

    @Override
    public Collection<BookCopy> findByBookId(Long bookId) throws SQLException {
        log.info("Request to find BookCopy by bookId {}", bookId);
        return bookCopyRepository.findByBookId(bookId);
    }

    @Override
    public Collection<BookCopy> findByEmail(String email) throws SQLException {
        log.info("Request to find BookCopy by email {}", email);
        return bookCopyRepository.findByEmail(email);
    }

    @Override
    public BookCopy save(BookCopy entity) throws SQLException {
        log.info("Request to save BookCopy {}", entity);
        return bookCopyRepository.save(entity);
    }

    @Override
    public BookCopy findById(Long id) throws SQLException {
        log.info("Request to find BookCopy with id {}", id);
        Optional<BookCopy> bookCopy = bookCopyRepository.findById(id);
        if (bookCopy.isPresent()) {
            return bookCopy.get();
        }
        throw new SQLException("BookCopy with id: {" + id + "}, not found");
    }

    @Override
    public BookCopy update(BookCopy entity) throws SQLException {
        log.info("Request to update BookCopy {}", entity);
        BookCopy foundBookCopy = findById(entity.getId());
        if (foundBookCopy.getBook().getId().equals(entity.getBook().getId())) {
            return bookCopyRepository.update(entity);
        }
        throw new IllegalArgumentException("While updating book copy is promised to change book_id");
    }

    @Override
    public void delete(BookCopy entity) throws SQLException {
        log.info("Request to delete BookCopy {}", entity);
        if (!bookCopyRepository.isBookCopyAvailable(entity.getId())) {
            throw new IllegalArgumentException("BookCopy with id : {" + entity.getId() + "}, is borrowed and cannot be deleted");
        }
        bookCopyRepository.delete(entity);
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        delete(findById(id));
    }

    @Override
    public Collection<BookCopy> findAll() throws SQLException {
        log.info("Request to find all BookCopies");
        return bookCopyRepository.findAll();
    }
}
