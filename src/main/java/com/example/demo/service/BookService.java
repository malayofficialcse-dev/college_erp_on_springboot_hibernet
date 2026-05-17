package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book bookDetails) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            Book existing = book.get();
            existing.setTitle(bookDetails.getTitle());
            existing.setAuthor(bookDetails.getAuthor());
            existing.setIsbn(bookDetails.getIsbn());
            existing.setStatus(bookDetails.getStatus());
            existing.setIssuedToStudent(bookDetails.getIssuedToStudent());
            existing.setIssueDate(bookDetails.getIssueDate());
            existing.setDueDate(bookDetails.getDueDate());
            return bookRepository.save(existing);
        }
        return null;
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
