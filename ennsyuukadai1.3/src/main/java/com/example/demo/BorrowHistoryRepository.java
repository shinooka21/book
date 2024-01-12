package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowHistoryRepository extends JpaRepository<BorrowHistory, Long> {
    BorrowHistory findByBookIdAndReturnDateIsNull(Long bookId);
    BorrowHistory findFirstByBookIdAndReturnDateIsNullOrderByBorrowDateDesc(Long bookId);
    List<BorrowHistory> findByBookIdOrderByBorrowDateDesc(Long bookId);
    List<BorrowHistory> findByBookIdAndReturnDateIsNotNullOrderByReturnDateDesc(Long bookId);

}
