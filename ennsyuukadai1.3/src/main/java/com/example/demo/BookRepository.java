package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
	public interface BookRepository extends JpaRepository<Book, Long> {
	
	 List<Book> findAll();
	 List<Book> findById(long parseLong);
	 Optional<Book> findByIsbn(String isbn);
}


