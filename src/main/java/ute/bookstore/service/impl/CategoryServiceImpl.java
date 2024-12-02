package ute.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ute.bookstore.entity.Category;
import ute.bookstore.exception.ResourceNotFoundException;
import ute.bookstore.repository.CategoryRepository;
import ute.bookstore.service.ICategoryService;
import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {

   @Autowired
   private CategoryRepository categoryRepository;
   
   @Override
   public List<Category> getAllCategories() {
       return categoryRepository.findAll();
   }

   @Override
   public Category getCategoryById(Long id) {
       return categoryRepository.findById(id)
           .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id)); 
   }

   @Override 
   public Category saveCategory(Category category) {
       return categoryRepository.save(category);
   }

   @Override
   public void deleteCategory(Long id) {
       categoryRepository.deleteById(id);
   }
}