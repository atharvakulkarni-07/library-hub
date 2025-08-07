package com.management.demo.service;

import com.management.demo.model.Book;
import com.management.demo.model.Member;
import com.management.demo.model.Transaction;
import com.management.demo.repository.BookRepository;
import com.management.demo.repository.MemberRepository;
import com.management.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    // Issue a book to a member
    public Transaction issueBook(Long bookId, Long memberId, int loanPeriodDays) {
        // Validate book exists and is available
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No copies available for book: " + book.getTitle());
        }

        // Validate member exists and is active
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        if (!member.getIsActive()) {
            throw new RuntimeException("Member account is inactive: " + member.getFirstName() + " " + member.getLastName());
        }

        // Create transaction
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(loanPeriodDays);

        Transaction transaction = new Transaction();
        transaction.setBookId(bookId);
        transaction.setMemberId(memberId);
        transaction.setIssueDate(today);
        transaction.setDueDate(dueDate);
        transaction.setReturnDate(null);
        transaction.setFineAmount(BigDecimal.ZERO);
        transaction.setStatus("ISSUED");

        // Save transaction
        Transaction savedTransaction = transactionRepository.insert(transaction);

        // Update book available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return savedTransaction;
    }

    // Return a book
    public Transaction returnBook(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));

        if (!"ISSUED".equals(transaction.getStatus())) {
            throw new RuntimeException("Book has already been returned");
        }

        LocalDate today = LocalDate.now();
        transaction.setReturnDate(today);

        // Calculate fine if overdue (₹5 per day)
        long daysLate = ChronoUnit.DAYS.between(transaction.getDueDate(), today);
        if (daysLate > 0) {
            BigDecimal fine = BigDecimal.valueOf(daysLate * 5);
            transaction.setFineAmount(fine);
            transaction.setStatus("OVERDUE");
        } else {
            transaction.setStatus("RETURNED");
        }

        // Update transaction
        Transaction updatedTransaction = transactionRepository.updateReturn(transaction);

        // Increase available book copies
        Book book = bookRepository.findById(transaction.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return updatedTransaction;
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Get transaction by ID
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    // Get transactions by member
    public List<Transaction> getTransactionsByMember(Long memberId) {
        return transactionRepository.findByMemberId(memberId);
    }

    // Get transactions by book
    public List<Transaction> getTransactionsByBook(Long bookId) {
        return transactionRepository.findByBookId(bookId);
    }

    // Get active (issued) transactions
    public List<Transaction> getActiveTransactions() {
        return transactionRepository.findActiveTransactions();
    }

    // Get overdue transactions
    public List<Transaction> getOverdueTransactions() {
        return transactionRepository.findOverdueTransactions();
    }
}
