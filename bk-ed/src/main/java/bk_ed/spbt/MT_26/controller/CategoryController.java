package bk_ed.spbt.MT_26.controller;

import bk_ed.spbt.MT_26.entity.AppUser;
import bk_ed.spbt.MT_26.entity.Category;
import bk_ed.spbt.MT_26.repository.UserRepository;
import bk_ed.spbt.MT_26.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        Category category = categoryService.createCategory(user, name, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllCategories(Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        List<Category> categories = categoryService.getAllCategoriesForUser(user);
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id, Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        Category category = categoryService.getCategoryById(id, user);
        return ResponseEntity.ok(category);
    }
    
    @GetMapping("/search/{name}")
    public ResponseEntity<?> searchCategoriesByName(@PathVariable String name, Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        List<Category> categories = categoryService.searchCategoriesByName(user, name);
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/count")
    public ResponseEntity<?> getCategoryCount(Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        long count = categoryService.getCategoryCount(user);
        Map<String, Long> response = new HashMap<>();
        response.put("totalCategories", count);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        Category category = categoryService.updateCategory(id, user, name, description);
        return ResponseEntity.ok(category);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        categoryService.deleteCategory(id, user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Category deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping
    public ResponseEntity<?> deleteMultipleCategories(
            @RequestBody List<Long> categoryIds,
            Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        categoryService.deleteMultipleCategories(user, categoryIds);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Categories deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/active/list")
    public ResponseEntity<?> getActiveCategories(Authentication authentication) {
        AppUser user = getUserFromAuthentication(authentication);
        List<Category> categories = categoryService.getActiveCategoriesForUser(user);
        return ResponseEntity.ok(categories);
    }
    
    private AppUser getUserFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
