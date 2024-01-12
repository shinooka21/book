package com.example.demo;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

@Entity
public class BorrowHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Book book;

    private LocalDateTime borrowDate;

    private LocalDateTime returnDate;

    public void setBook(Book book) {
        this.book = book;
    }

    public void setReturnDate(LocalDateTime now) {
        this.returnDate = now;
    }

    public void setBorrowDate(LocalDateTime now) {
        this.borrowDate = now;
    }

	public Long getId() {
		return id;
	}

	public Book getBook() {
		return book;
	}

	public LocalDateTime getBorrowDate() {
		return borrowDate;
	}

	public LocalDateTime getReturnDate() {
		return returnDate;
	}
    
    
}
