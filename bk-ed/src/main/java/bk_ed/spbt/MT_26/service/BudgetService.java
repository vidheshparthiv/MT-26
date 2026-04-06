package bk_ed.spbt.MT_26.service;

import bk_ed.spbt.MT_26.entity.AppUser;
import bk_ed.spbt.MT_26.entity.Budget;
import bk_ed.spbt.MT_26.entity.Transaction;
import bk_ed.spbt.MT_26.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BudgetService {
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private TransactionService transactionService;
    
    public Budget createBudget(AppUser user, String name, String description, BigDecimal limitAmount, String period) {
        if (budgetRepository.findByUserAndName(user, name).isPresent()) {
            throw new RuntimeException("Budget with this name already exists");
        }
        Budget budget = new Budget(user, name, description, limitAmount, period);
        return budgetRepository.save(budget);
    }
    
    public List<Budget> getAllBudgetsForUser(AppUser user) {
        return budgetRepository.findByUser(user);
    }
    
    public Budget getBudgetById(Long budgetId, AppUser user) {
        return budgetRepository.findByIdAndUser(budgetId, user)
                .orElseThrow(() -> new RuntimeException("Budget not found or access denied"));
    }
    
    public List<Budget> getBudgetsByPeriod(AppUser user, String period) {
        return budgetRepository.findByUserAndPeriod(user, period);
    }
    
    public Budget updateBudget(Long budgetId, AppUser user, String name, String description, BigDecimal limitAmount, String period) {
        Budget budget = getBudgetById(budgetId, user);
        budget.setName(name);
        budget.setDescription(description);
        budget.setLimitAmount(limitAmount);
        budget.setPeriod(period);
        budget.setUpdatedAt(LocalDateTime.now());
        
        return budgetRepository.save(budget);
    }
    
    public void deleteBudget(Long budgetId, AppUser user) {
        Budget budget = getBudgetById(budgetId, user);
        budgetRepository.delete(budget);
    }
    
    public List<Budget> searchBudgetsByName(AppUser user, String name) {
        return budgetRepository.findByUserAndNameContainingIgnoreCase(user, name);
    }
    
    public long getBudgetCount(AppUser user) {
        return budgetRepository.countByUser(user);
    }
    
    public void deleteMultipleBudgets(AppUser user, List<Long> budgetIds) {
        for (Long budgetId : budgetIds) {
            deleteBudget(budgetId, user);
        }
    }
    
    public Map<String, Object> getBudgetSummary(AppUser user) {
        List<Budget> budgets = getAllBudgetsForUser(user);
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalBudgets", budgets.size());
        summary.put("budgets", budgets);
        return summary;
    }
}
