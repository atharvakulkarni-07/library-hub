package com.management.demo.controller;

import com.management.demo.model.Transaction;
import com.management.demo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // POST /api/transactions/issue - Issue a book
    @PostMapping("/issue")
    public ResponseEntity<?> issueBook(@RequestParam Long bookId,
                                       @RequestParam Long memberId,
                                       @RequestParam(defaultValue = "14") int loanPeriodDays) {
        try {
            Transaction transaction = transactionService.issueBook(bookId, memberId, loanPeriodDays);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (RuntimeException e) {
            System.err.println("Error issuing book: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // PUT /api/transactions/{id}/return - Return a book
    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        try {
            Transaction transaction = transactionService.returnBook(id);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            System.err.println("Error returning book: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // GET /api/transactions - Get all transactions
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    // GET /api/transactions/{id} - Get transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/transactions/member/{memberId} - Get member's transactions
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Transaction>> getTransactionsByMember(@PathVariable Long memberId) {
        List<Transaction> transactions = transactionService.getTransactionsByMember(memberId);
        return ResponseEntity.ok(transactions);
    }

    // GET /api/transactions/book/{bookId} - Get book's transaction history
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Transaction>> getTransactionsByBook(@PathVariable Long bookId) {
        List<Transaction> transactions = transactionService.getTransactionsByBook(bookId);
        return ResponseEntity.ok(transactions);
    }

    // GET /api/transactions/active - Get all active (issued) transactions
    @GetMapping("/active")
    public ResponseEntity<List<Transaction>> getActiveTransactions() {
        List<Transaction> transactions = transactionService.getActiveTransactions();
        return ResponseEntity.ok(transactions);
    }

    // GET /api/transactions/overdue - Get overdue transactions
    @GetMapping("/overdue")
    public ResponseEntity<List<Transaction>> getOverdueTransactions() {
        List<Transaction> transactions = transactionService.getOverdueTransactions();
        return ResponseEntity.ok(transactions);
    }
}
