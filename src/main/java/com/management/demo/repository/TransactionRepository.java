package com.management.demo.repository;

import com.management.demo.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Transaction> transactionRowMapper = (rs, rowNum) -> {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getLong("id"));
        transaction.setBookId(rs.getLong("book_id"));
        transaction.setMemberId(rs.getLong("member_id"));
        transaction.setIssueDate(rs.getDate("issue_date").toLocalDate());
        transaction.setDueDate(rs.getDate("due_date").toLocalDate());

        if (rs.getDate("return_date") != null) {
            transaction.setReturnDate(rs.getDate("return_date").toLocalDate());
        }

        transaction.setFineAmount(rs.getBigDecimal("fine_amount"));
        transaction.setStatus(rs.getString("status"));
        transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return transaction;
    };

    // Find all transactions
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper);
    }

    // Find transaction by ID
    public Optional<Transaction> findById(Long id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        List<Transaction> transactions = jdbcTemplate.query(sql, transactionRowMapper, id);
        return transactions.isEmpty() ? Optional.empty() : Optional.of(transactions.get(0));
    }

    // Find transactions by member
    public List<Transaction> findByMemberId(Long memberId) {
        String sql = "SELECT * FROM transactions WHERE member_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, memberId);
    }

    // Find transactions by book
    public List<Transaction> findByBookId(Long bookId) {
        String sql = "SELECT * FROM transactions WHERE book_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, transactionRowMapper, bookId);
    }

    // Insert new transaction
    public Transaction insert(Transaction transaction) {
        String sql = """
            INSERT INTO transactions 
            (book_id, member_id, issue_date, due_date, return_date, fine_amount, status, created_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?) 
            RETURNING id
            """;

        LocalDateTime now = LocalDateTime.now();

        Integer generatedId = jdbcTemplate.queryForObject(sql, Integer.class,
                transaction.getBookId(),
                transaction.getMemberId(),
                transaction.getIssueDate(),
                transaction.getDueDate(),
                transaction.getReturnDate(),
                transaction.getFineAmount(),
                transaction.getStatus(),
                now
        );

        transaction.setId(generatedId.longValue());
        transaction.setCreatedAt(now);

        return transaction;
    }

    // Update transaction (for returns)
    public Transaction updateReturn(Transaction transaction) {
        String sql = """
            UPDATE transactions 
            SET return_date = ?, fine_amount = ?, status = ? 
            WHERE id = ?
            """;

        jdbcTemplate.update(sql,
                transaction.getReturnDate(),
                transaction.getFineAmount(),
                transaction.getStatus(),
                transaction.getId()
        );

        return transaction;
    }

    // Find active transactions (not returned)
    public List<Transaction> findActiveTransactions() {
        String sql = "SELECT * FROM transactions WHERE status = 'ISSUED' ORDER BY due_date";
        return jdbcTemplate.query(sql, transactionRowMapper);
    }

    // Find overdue transactions
    public List<Transaction> findOverdueTransactions() {
        String sql = "SELECT * FROM transactions WHERE status = 'ISSUED' AND due_date < CURRENT_DATE ORDER BY due_date";
        return jdbcTemplate.query(sql, transactionRowMapper);
    }
}
