package com.sbbc.sl.librarymanagement.service;

import com.sbbc.sl.librarymanagement.entity.Book;
import com.sbbc.sl.librarymanagement.entity.IssueRecord;
import com.sbbc.sl.librarymanagement.entity.User;
import com.sbbc.sl.librarymanagement.repository.BooksRepository;
import com.sbbc.sl.librarymanagement.repository.IssueRecordRepository;
import com.sbbc.sl.librarymanagement.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class IssueRecordService {

    private final IssueRecordRepository issueRecordRepository;
    private final BooksRepository booksRepository;
    private final UserRepository userRepository;

    public IssueRecordService(IssueRecordRepository issueRecordRepository,
                              BooksRepository booksRepository,
                              UserRepository userRepository) {
        this.issueRecordRepository = issueRecordRepository;
        this.booksRepository = booksRepository;
        this.userRepository = userRepository;
    }

    public IssueRecord issueTheBook(Long bookId) {
        log.info("Attempting to issue book with ID: {}", bookId);
        Book book = booksRepository.findById(bookId)
                .orElseThrow(() -> {
                    log.warn("Book not found with ID: {}", bookId);
                    return new RuntimeException("Book not found with id " + bookId);
                });

        if (book.getQuantity() <= 0 || !book.getIsAvailable()) {
            log.warn("Book with ID {} is not available for issuing", bookId);
            throw new RuntimeException("Book is not available");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Authenticated user: {}", username);
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new RuntimeException("User not found with username " + username);
                });

        IssueRecord issueRecord = new IssueRecord();
        issueRecord.setIssueDate(LocalDate.now());
        issueRecord.setDueDate(LocalDate.now().plusDays(14));
        issueRecord.setIsReturned(false);
        issueRecord.setBook(book);
        issueRecord.setUser(user);

        book.setQuantity(book.getQuantity() - 1);
        if (book.getQuantity() <= 0) {
            book.setIsAvailable(false);
        }

        booksRepository.save(book);
        IssueRecord savedRecord = issueRecordRepository.save(issueRecord);

        log.info("Book with ID {} successfully issued to user {}. Issue record ID: {}",
                bookId, username, savedRecord.getId());

        return savedRecord;
    }

    public IssueRecord returnTheBook(Long issueRecordId) {
        log.info("Attempting to return book with issue record ID: {}", issueRecordId);
        IssueRecord issueRecord = issueRecordRepository.findById(issueRecordId)
                .orElseThrow(() -> {
                    log.warn("Issue record not found with ID: {}", issueRecordId);
                    return new RuntimeException("Issue record not found with id " + issueRecordId);
                });

        if (issueRecord.getIsReturned()) {
            log.warn("Book with issue record ID {} is already returned", issueRecordId);
            throw new RuntimeException("Book is already returned");
        }

        Book book = issueRecord.getBook();
        book.setQuantity(book.getQuantity() + 1);
        book.setIsAvailable(true);
        booksRepository.save(book);

        issueRecord.setReturnDate(LocalDate.now());
        issueRecord.setIsReturned(true);
        IssueRecord savedReturn = issueRecordRepository.save(issueRecord);

        log.info("Book with ID {} returned by user {}. Issue record ID: {}",
                book.getId(), issueRecord.getUser().getUsername(), issueRecordId);

        return savedReturn;
    }
}
