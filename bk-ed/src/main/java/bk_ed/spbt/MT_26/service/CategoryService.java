package bk_ed.spbt.MT_26.service;

import bk_ed.spbt.MT_26.entity.AppUser;
import bk_ed.spbt.MT_26.entity.Category;
import bk_ed.spbt.MT_26.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public Category createCategory(AppUser user, String name, String description) {
        if (categoryRepository.existsByNameAndUser(name, user)) {
            throw new RuntimeException("Category with this name already exists");
        }
        
        Category category = new Category(name, description, user);
        return categoryRepository.save(category);
    }
    
    public List<Category> getAllCategoriesForUser(AppUser user) {
        return categoryRepository.findByUser(user);
    }
    
    public Category getCategoryById(Long categoryId, AppUser user) {
        return categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new RuntimeException("Category not found or access denied"));
    }
    
    public Category updateCategory(Long categoryId, AppUser user, String name, String description) {
        Category category = getCategoryById(categoryId, user);
        category.setName(name);
        category.setDescription(description);
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }
    
    public void deleteCategory(Long categoryId, AppUser user) {
        Category category = getCategoryById(categoryId, user);
        categoryRepository.delete(category);
    }
}
