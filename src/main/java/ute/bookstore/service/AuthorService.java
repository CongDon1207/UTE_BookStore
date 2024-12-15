package ute.bookstore.service;

import java.util.List;

import ute.bookstore.entity.Author;

public interface AuthorService {

	List<Author> findAll();

	Author findById(Long authorId);

}
