package com.management.demo.repository;

import com.management.demo.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper to convert database rows to Book objects
    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setCategory(rs.getString("category"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setTotalCopies(rs.getInt("total_copies"));
        book.setAvailableCopies(rs.getInt("available_copies"));

        // Handle null timestamps gracefully
        if (rs.getTimestamp("created_at") != null) {
            book.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            book.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return book;
    };

    // Find all books
    public List<Book> findAll() {
        String sql = "SELECT * FROM books ORDER BY title";
        return jdbcTemplate.query(sql, bookRowMapper);
    }

    // Find book by ID
    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper, id);
        return books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));
    }

    // Find book by ISBN
    public Optional<Book> findByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper, isbn);
        return books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));
    }

    // Save new book
    public Book save(Book book) {
        if (book.getId() == null) {
            return insert(book);
        } else {
            return update(book);
        }
    }

    // Insert new book
    // In BookRepository.java, update the insert method
    // In BookRepository.java, update the insert method
    private Book insert(Book book) {
        String sql = "INSERT INTO books (isbn, title, author, category, publication_year, total_copies, available_copies, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getCategory());
            ps.setInt(5, book.getPublicationYear());
            ps.setInt(6, book.getTotalCopies());
            ps.setInt(7, book.getAvailableCopies());
            ps.setObject(8, now);
            ps.setObject(9, now);
            return ps;
        }, keyHolder);

        // FIX: Handle both Integer and Long cases
        Number keyValue = (Number) keyHolder.getKeys().get("id");
        Long generatedId = keyValue.longValue();

        book.setId(generatedId);
        book.setCreatedAt(now);
        book.setUpdatedAt(now);

        return book;
    }


    // Update existing book
    private Book update(Book book) {
        String sql = "UPDATE books SET isbn = ?, title = ?, author = ?, category = ?, publication_year = ?, total_copies = ?, available_copies = ?, updated_at = ? WHERE id = ?";

        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(sql,
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getPublicationYear(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                now,
                book.getId()
        );

        book.setUpdatedAt(now);
        return book;
    }

    // Delete book
    public void deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Search books by title or author
    public List<Book> searchBooks(String searchTerm) {
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?) OR LOWER(author) LIKE LOWER(?) ORDER BY title";
        String searchPattern = "%" + searchTerm + "%";
        return jdbcTemplate.query(sql, bookRowMapper, searchPattern, searchPattern);
    }
}
