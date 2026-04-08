package bk_ed.spbt.MT_26.controller;

import bk_ed.spbt.MT_26.entity.AppUser;
import bk_ed.spbt.MT_26.entity.Budget;
import bk_ed.spbt.MT_26.repository.UserRepository;
import bk_ed.spbt.MT_26.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    
    @Autowired
    private BudgetService budgetService;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<?> createBudget(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam BigDecimal limitAmount,
            @RequestParam String period,
            Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        Budget budget = budgetService.createBudget(user, name, description, limitAmount, period);
        return ResponseEntity.status(HttpStatus.CREATED).body(budget);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllBudgets(Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        List<Budget> budgets = budgetService.getAllBudgetsForUser(user);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getBudgetById(@PathVariable Long id, Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        Budget budget = budgetService.getBudgetById(id, user);
        return ResponseEntity.ok(budget);
    }
    @GetMapping("/period/{period}")
    public ResponseEntity<?> getBudgetsByPeriod(@PathVariable String period, Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        List<Budget> budgets = budgetService.getBudgetsByPeriod(user, period);
        return ResponseEntity.ok(budgets);
    } 
    @GetMapping("/search/{name}")
    public ResponseEntity<?> searchBudgetsByName(@PathVariable String name, Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        List<Budget> budgets = budgetService.searchBudgetsByName(user, name);
        return ResponseEntity.ok(budgets);
    }
    @GetMapping("/count")
    public ResponseEntity<?> getBudgetCount(Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        long count = budgetService.getBudgetCount(user);
        Map<String, Long> response = new HashMap<>();
        response.put("totalBudgets", count);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/summary/spent")
    public ResponseEntity<?> getBudgetSummary(Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        Map<String, Object> summary = budgetService.getBudgetSummary(user);
        return ResponseEntity.ok(summary);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam BigDecimal limitAmount,
            @RequestParam String period,
            Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        Budget budget = budgetService.updateBudget(id, user, name, description, limitAmount, period);
        return ResponseEntity.ok(budget);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id, Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        budgetService.deleteBudget(id, user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Budget deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping
    public ResponseEntity<?> deleteMultipleBudgets(
            @RequestBody List<Long> budgetIds,
            Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        budgetService.deleteMultipleBudgets(user, budgetIds);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Budgets deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    private AppUser getUserFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
