package ute.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ute.bookstore.entity.BookAuthor;

import java.util.List;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, Long> {
    List<BookAuthor> findByAuthorId(Long authorId);
}
