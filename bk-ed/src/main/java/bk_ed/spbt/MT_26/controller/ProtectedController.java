package bk_ed.spbt.MT_26.controller;

import bk_ed.spbt.MT_26.entity.AppUser;
import bk_ed.spbt.MT_26.repository.UserRepository;
import bk_ed.spbt.MT_26.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProtectedController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/protected")
    public ResponseEntity<?> protectedEndpoint() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Access granted!");
        response.put("username", username);
        response.put("status", "authenticated");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            AppUser user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("role", user.getRole());
            response.put("roles", auth.getAuthorities());
            response.put("authenticated", auth.isAuthenticated());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/hello")
    public ResponseEntity<?> helloUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        return ResponseEntity.ok("Hello " + username + "! Your JWT token is working! ✅");
    }
    
    @GetMapping("/dashboard/summary")
    public ResponseEntity<?> getDashboardSummary() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            AppUser user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            BigDecimal totalIncome = transactionService.getTotalIncome(user);
            BigDecimal totalExpenses = transactionService.getTotalExpenses(user);
            BigDecimal balance = transactionService.getBalance(user);
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("userId", user.getId());
            summary.put("username", user.getUsername());
            summary.put("totalIncome", totalIncome);
            summary.put("totalExpenses", totalExpenses);
            summary.put("balance", balance);
            summary.put("message", "Dashboard summary retrieved successfully");
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve dashboard: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
