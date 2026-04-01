package bk_ed.spbt.MT_26.controller;

import bk_ed.spbt.MT_26.entity.AppUser;
import bk_ed.spbt.MT_26.entity.Transaction;
import bk_ed.spbt.MT_26.repository.UserRepository;
import bk_ed.spbt.MT_26.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<?> createTransaction(
            @RequestParam Long categoryId,
            @RequestParam BigDecimal amount,
            @RequestParam String type,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime transactionDate,
            Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        Transaction transaction = transactionService.createTransaction(user, categoryId, amount, type, description, transactionDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllTransactions(Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        List<Transaction> transactions = transactionService.getAllTransactionsForUser(user);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id, Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        Transaction transaction = transactionService.getTransactionById(id, user);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getTransactionsByType(@PathVariable String type, Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        List<Transaction> transactions = transactionService.getTransactionsByType(user, type);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getTransactionsByCategory(@PathVariable Long categoryId, Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        List<Transaction> transactions = transactionService.getTransactionsByCategory(user, categoryId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<?> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(user, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/search/{description}")
    public ResponseEntity<?> searchTransactionsByDescription(@PathVariable String description, Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        List<Transaction> transactions = transactionService.searchTransactionsByDescription(user, description);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/count")
    public ResponseEntity<?> getTransactionCount(Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        long count = transactionService.getTransactionCount(user);
        Map<String, Long> response = new HashMap<>();
        response.put("totalTransactions", count);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable Long id,
            @RequestParam Long categoryId,
            @RequestParam BigDecimal amount,
            @RequestParam String type,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime transactionDate,
            Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        Transaction transaction = transactionService.updateTransaction(id, user, categoryId, amount, type, description, transactionDate);
        return ResponseEntity.ok(transaction);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id, Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        transactionService.deleteTransaction(id, user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Transaction deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping
    public ResponseEntity<?> deleteMultipleTransactions(
            @RequestBody List<Long> transactionIds,
            Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        transactionService.deleteMultipleTransactions(user, transactionIds);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Transactions deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/summary/balance")
    public ResponseEntity<?> getBalance(Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        BigDecimal totalIncome = transactionService.getTotalIncome(user);
        BigDecimal totalExpenses = transactionService.getTotalExpenses(user);
        BigDecimal balance = transactionService.getBalance(user);
        
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("totalIncome", totalIncome);
        response.put("totalExpenses", totalExpenses);
        response.put("balance", balance);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/summary/monthly")
    public ResponseEntity<?> getMonthlySummary(Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        Map<String, Object> summary = transactionService.getMonthlySummary(user);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/average/amount")
    public ResponseEntity<?> getAverageTransactionAmount(Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        BigDecimal average = transactionService.getAverageTransactionAmount(user);
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("averageAmount", average);
        return ResponseEntity.ok(response);
    }
    
    private AppUser getUserFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
