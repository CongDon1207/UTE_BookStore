package ute.bookstore.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ute.bookstore.entity.Author;
import ute.bookstore.repository.AuthorRepository;
import ute.bookstore.service.AuthorService;

@Service
public class AuthorServiceImpl implements AuthorService {
	@Autowired
	private AuthorRepository authorRepository;

	@Override
	public List<Author> findAll() {
		return authorRepository.findAll();
	}

	@Override
	public Author findById(Long authorId) {
		// TODO Auto-generated method stub
		return authorRepository.findById(authorId).orElse(null);
	}

}
