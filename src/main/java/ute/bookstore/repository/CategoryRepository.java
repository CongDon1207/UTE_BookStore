package ute.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ute.bookstore.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
