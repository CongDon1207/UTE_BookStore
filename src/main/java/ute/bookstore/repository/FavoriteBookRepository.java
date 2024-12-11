package ute.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ute.bookstore.entity.Book;
import ute.bookstore.entity.FavoriteBook;
import ute.bookstore.entity.User;

@Repository
public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Long> {
    boolean existsByUserAndBook(User user, Book book);
    Optional<FavoriteBook> findByUserAndBook(User user, Book book);
}
