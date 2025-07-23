package com.sbbc.sl.librarymanagement.repository;

import com.sbbc.sl.librarymanagement.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BooksRepository extends JpaRepository<Book, Long> {
    Book getBookById(Long id);
}
