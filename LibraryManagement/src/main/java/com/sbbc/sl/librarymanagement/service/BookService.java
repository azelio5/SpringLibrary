package com.sbbc.sl.librarymanagement.service;

import com.sbbc.sl.librarymanagement.dto.BookDTO;
import com.sbbc.sl.librarymanagement.entity.Book;
import com.sbbc.sl.librarymanagement.repository.BooksRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookService {

    private final BooksRepository booksRepository;

    public BookService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> getAllBooks() {
        log.info("Getting all books");
        return booksRepository.findAll();
    }

    public Book getBookById(Long id) {
        log.info("Getting book with id: {}", id);
        return booksRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found with id: {}", id);
                    return new RuntimeException("Book not found with id: " + id);
                });
    }

    public Book addBook(BookDTO bookDTO) {
        log.info("Adding new book: {}", bookDTO.getTitle());
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setQuantity(bookDTO.getQuantity());
        book.setIsAvailable(bookDTO.getIsAvailable());
        return booksRepository.save(book);
    }

    public Book updateBook(Long id, BookDTO bookDTO) {
        log.info("Updating book with id: {}", id);
        Book book = booksRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found with id: {}", id);
                    return new RuntimeException("Book not found with id: " + id);
                });
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setQuantity(bookDTO.getQuantity());
        book.setIsAvailable(bookDTO.getIsAvailable());
        return booksRepository.save(book);
    }

    public void deleteBookById(Long id) {
        log.info("Attempting to delete book with id: {}", id);
        if (!booksRepository.existsById(id)) {
            log.warn("Book not found for deletion with id: {}", id);
            throw new RuntimeException("Book not found with id: " + id);
        }
        booksRepository.deleteById(id);
        log.info("Book with id {} successfully deleted", id);
    }
}
