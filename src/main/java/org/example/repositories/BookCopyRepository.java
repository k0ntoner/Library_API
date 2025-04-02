package org.example.repositories;

import org.example.entities.BookCopy;

import java.sql.SQLException;
import java.util.Collection;

public interface BookCopyRepository extends BasicRepository<BookCopy> {
    Collection<BookCopy> findByEmail(String email) throws SQLException;

    Collection<BookCopy> findByBookId(Long bookId) throws SQLException;

    boolean isBookCopyAvailable(Long id) throws SQLException;
}
