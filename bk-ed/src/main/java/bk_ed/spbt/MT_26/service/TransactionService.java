package bk_ed.spbt.MT_26.service;

import bk_ed.spbt.MT_26.entity.AppUser;
import bk_ed.spbt.MT_26.entity.Category;
import bk_ed.spbt.MT_26.entity.Transaction;
import bk_ed.spbt.MT_26.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CategoryService categoryService;
    
    public Transaction createTransaction(AppUser user, Long categoryId, BigDecimal amount, 
                                       String type, String description, LocalDateTime transactionDate) {
        Category category = categoryService.getCategoryById(categoryId, user);
        
        Transaction transaction = new Transaction(user, category, amount, type, description);
        if (transactionDate != null) {
            transaction.setTransactionDate(transactionDate);
        }
        
        return transactionRepository.save(transaction);
    }
    
    public List<Transaction> getAllTransactionsForUser(AppUser user) {
        return transactionRepository.findByUser(user);
    }
    
    public Transaction getTransactionById(Long transactionId, AppUser user) {
        return transactionRepository.findByIdAndUser(transactionId, user)
                .orElseThrow(() -> new RuntimeException("Transaction not found or access denied"));
    }
    
    public List<Transaction> getTransactionsByType(AppUser user, String type) {
        return transactionRepository.findByUserAndType(user, type);
    }
    
    public List<Transaction> getTransactionsByCategory(AppUser user, Long categoryId) {
        categoryService.getCategoryById(categoryId, user);
        return transactionRepository.findByUserAndCategory(user, categoryId);
    }
    
    public List<Transaction> getTransactionsByDateRange(AppUser user, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByUserAndDateRange(user, startDate, endDate);
    }
    
    public Transaction updateTransaction(Long transactionId, AppUser user, Long categoryId, BigDecimal amount, 
                                        String type, String description, LocalDateTime transactionDate) {
        Transaction transaction = getTransactionById(transactionId, user);
        Category category = categoryService.getCategoryById(categoryId, user);
        
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDescription(description);
        if (transactionDate != null) {
            transaction.setTransactionDate(transactionDate);
        }
        transaction.setUpdatedAt(LocalDateTime.now());
        
        return transactionRepository.save(transaction);
    }
    
    public void deleteTransaction(Long transactionId, AppUser user) {
        Transaction transaction = getTransactionById(transactionId, user);
        transactionRepository.delete(transaction);
    }
    
    public BigDecimal getTotalIncome(AppUser user) {
        List<Transaction> transactions = transactionRepository.findByUserAndType(user, "INCOME");
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getTotalExpenses(AppUser user) {
        List<Transaction> transactions = transactionRepository.findByUserAndType(user, "EXPENSE");
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getBalance(AppUser user) {
        BigDecimal totalIncome = getTotalIncome(user);
        BigDecimal totalExpenses = getTotalExpenses(user);
        return totalIncome.subtract(totalExpenses);
    }
}
 