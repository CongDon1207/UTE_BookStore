package ute.bookstore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ute.bookstore.entity.BookAuthor;
import ute.bookstore.repository.BookAuthorRepository;
import ute.bookstore.service.bookAuthorService;

@Service
public class bookAuthorServiceImpl implements bookAuthorService{
	@Autowired
	BookAuthorRepository bookAuthorRepository;
	@Override
	public List<BookAuthor> findByAuthorId(Long authorId) {
		return bookAuthorRepository.findByAuthorId(authorId);
	}

}
