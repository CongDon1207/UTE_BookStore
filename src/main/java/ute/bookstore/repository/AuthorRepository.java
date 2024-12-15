package ute.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ute.bookstore.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
