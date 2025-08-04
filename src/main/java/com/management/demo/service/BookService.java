package com.management.demo.service;

import com.management.demo.model.Book;
import com.management.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // Get all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Get book by ID
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // Get book by ISBN
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    // Add new book
    public Book addBook(Book book) {
        // Check if ISBN already exists
        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new RuntimeException("Book with ISBN " + book.getIsbn() + " already exists");
        }

        // Set default values if not provided
        if (book.getTotalCopies() == null) {
            book.setTotalCopies(1);
        }
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }

        return bookRepository.save(book);
    }

    // Update book
    public Book updateBook(Long id, Book bookDetails) {
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isEmpty()) {
            throw new RuntimeException("Book not found with id: " + id);
        }

        Book book = existingBook.get();
        book.setIsbn(bookDetails.getIsbn());
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setCategory(bookDetails.getCategory());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setTotalCopies(bookDetails.getTotalCopies());
        book.setAvailableCopies(bookDetails.getAvailableCopies());

        return bookRepository.save(book);
    }

    // Delete book
    public void deleteBook(Long id) {
        if (bookRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    // Search books
    public List<Book> searchBooks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookRepository.searchBooks(searchTerm.trim());
    }

    // Check if book is available for borrowing
    public boolean isBookAvailable(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        return book.isPresent() && book.get().getAvailableCopies() > 0;
    }
}
