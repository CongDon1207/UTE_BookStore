package ute.bookstore.service;

import java.util.List;
import ute.bookstore.entity.Category;

public interface ICategoryService {
   List<Category> getAllCategories();
   Category getCategoryById(Long id); 
   Category saveCategory(Category category);
   void deleteCategory(Long id);
}

