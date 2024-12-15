package ute.bookstore.service;

import java.util.List;

import ute.bookstore.entity.BookAuthor;

public interface bookAuthorService {

	List<BookAuthor> findByAuthorId(Long authorId);

}
