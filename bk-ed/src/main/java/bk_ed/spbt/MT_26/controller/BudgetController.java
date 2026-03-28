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
        try {
            AppUser user = getUserFromAuthentication(authentication);
            Budget budget = budgetService.createBudget(user, name, description, limitAmount, period);
            return ResponseEntity.status(HttpStatus.CREATED).body(budget);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllBudgets(Authentication authentication) {
        try {
            AppUser user = getUserFromAuthentication(authentication);
            List<Budget> budgets = budgetService.getAllBudgetsForUser(user);
            return ResponseEntity.ok(budgets);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getBudgetById(@PathVariable Long id, Authentication authentication) {
        try {
            AppUser user = getUserFromAuthentication(authentication);
            Budget budget = budgetService.getBudgetById(id, user);
            return ResponseEntity.ok(budget);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @GetMapping("/period/{period}")
    public ResponseEntity<?> getBudgetsByPeriod(@PathVariable String period, Authentication authentication) {
        try {
            AppUser user = getUserFromAuthentication(authentication);
            List<Budget> budgets = budgetService.getBudgetsByPeriod(user, period);
            return ResponseEntity.ok(budgets);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam BigDecimal limitAmount,
            @RequestParam String period,
            Authentication authentication) {
        try {
            AppUser user = getUserFromAuthentication(authentication);
            Budget budget = budgetService.updateBudget(id, user, name, description, limitAmount, period);
            return ResponseEntity.ok(budget);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id, Authentication authentication) {
        try {
            AppUser user = getUserFromAuthentication(authentication);
            budgetService.deleteBudget(id, user);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Budget deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    private AppUser getUserFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
