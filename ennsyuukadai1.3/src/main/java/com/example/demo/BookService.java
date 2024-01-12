package com.example.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

//操作するクラス
@Service
public class BookService {
	//リポジトリにアクセスできるようにする
	@Autowired
    private BookRepository bookRepository;
    @Autowired
    private BorrowHistoryRepository borrowHistoryRepository;

	//全書籍の情報を取得する
	public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
	//書籍の情報を保存するためのメソッド
	
	public Book createBook(Book book) {
		//データベースに新しい書籍（Book エンティティ）を作成・保存するためのメソッド
        return bookRepository.save(book);
        //このメソッドはエンティティをデータベースに保存するために使用される。
    }
	
	
	 // 書籍を更新するメソッド
	public void updateBook(Book updatedBook) {
		
		 // 更新する書籍をIDで検索
        Optional<Book> existingBookOptional = bookRepository.findById(updatedBook.getId());

        if (existingBookOptional.isPresent()) {
            // 書籍が存在する場合
            Book existingBook = existingBookOptional.get();
            
            // 更新内容を設定
            existingBook.setTitle(updatedBook.getTitle());
            existingBook.setAuthor(updatedBook.getAuthor());
            existingBook.setIsbn(updatedBook.getIsbn());
            existingBook.setStatus(updatedBook.getStatus());

            // 書籍を保存
            bookRepository.save(existingBook);
        } else {
            // 書籍が存在しない場合のエラー処理
            throw new RuntimeException("書籍が見つかりません: " + updatedBook.getId());
        }
    }
	
	//指定された書籍のidを取得するメソッド
	public Book getBookById(Long id) {
	       return bookRepository.findById(id).orElse(null);
	   }
	
	//検索結果を表示させるためのメソッド
	public List<Book> searchBooks(String id) {
	    if (id == null || id.trim().isEmpty()) {
	        // IDが指定されていない場合は全ての書籍を取得
	        return bookRepository.findAll();
	    }

	    try {
	        if (id.chars().allMatch(Character::isDigit)) {
	            // 数字の場合、IDをLongに変換して該当の書籍を取得
	            Long parsedId = Long.parseLong(id);
	            Book book = bookRepository.findById(parsedId).orElse(null);
	            if (book != null) {
	                return Collections.singletonList(book);
	            } else {
	                // 書籍が見つからなかった場合
	                throw new IllegalArgumentException("書籍が見つかりませんでした");
	            }
	        } else {
	            // 数字以外の形式のIDが渡された場合
	            throw new IllegalArgumentException("Invalid ID format");
	        }
	    } catch (NumberFormatException e) {
	        // 数字に変換できなかった場合
	        throw new IllegalArgumentException("Invalid ID format");
	    }
	}
	
	// 書籍を削除するメソッド
    public void deleteBookById(Long id) {
        // IDに基づいて書籍を削除
        if (existsBookById(id)) {
            bookRepository.deleteById(id);
        } else {
            throw new RuntimeException("書籍が見つかりません: " + id);
        }
    }

    // IDに基づいて書籍が存在するかどうかを確認するメソッド
    public boolean existsBookById(Long id) {
        return bookRepository.existsById(id);
    }

    // IDに基づいて書籍を取得するメソッド
    public Book fetchBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("書籍が見つかりません: " + id));
    }
	    
	    public Book findExistingBookByIsbn(String isbn) {
	        // ISBNが一致する書籍が既に存在するか確認する
	        return bookRepository.findByIsbn(isbn).orElse(null);
	    }
	 //新規登録をするためのメソッド
	    public void insertBook(String title, String author, String isbn) {
	    	Book existingBook = findExistingBookByIsbn(isbn);
	        if (existingBook == null) {
	    	Book book = new Book();
	        book.setTitle(title);
	        book.setAuthor(author);
	        book.setIsbn(isbn); 
	        book.setStatus("AVAILABLE");
	        bookRepository.save(book);
	        
	        } else {
	            // 重複がある場合の処理（エラーメッセージの表示など）
	            System.out.println("この書籍は既に登録されています。");
	        }
	    }
	    
	    
	    
	  //貸し出し履歴を取得するメソッド
	    public List<BorrowHistory> getBorrowHistory(Long bookId) {
	        return borrowHistoryRepository.findByBookIdOrderByBorrowDateDesc(bookId);
	    }
	    
	    
	    
	    //貸出・返却機能
	    //現在の状況を入力して借りてるなら貸出中とかメッセージを返す処理・細かく状況も説明して聞く。
	    //貸出結果のメッセージを取得
	    
	    
	    
	    public String lendBookAndUpdateStatus(Long bookId) {
	        Optional<Book> optionalBook = bookRepository.findById(bookId);
	        if (optionalBook.isPresent()) {
	            Book book = optionalBook.get();
	            if ("AVAILABLE".equals(book.getStatus())) {
	                // 貸出可能な状態の場合の処理
	                book.setStatus("BORROWED");
	                bookRepository.save(book);

	                BorrowHistory borrowHistory = new BorrowHistory();
	                borrowHistory.setBook(book);
	                borrowHistory.setBorrowDate(LocalDateTime.now());
	                borrowHistoryRepository.save(borrowHistory);

	                return "貸出しました。";
	            } else {
	                return "貸出できません。"; // 例えばすでに貸出中の場合など
	            }
	        } else {
	            return "書籍が存在しません。"; // ブックIDが存在しない場合など
	        }
	    }

	    public ModelAndView getBookListView() {
	        List<Book> books = bookRepository.findAll();
	        ModelAndView modelAndView = new ModelAndView();
	        modelAndView.setViewName("index");
	        modelAndView.addObject("books", books);
	        return modelAndView;
	    }
	    
	   //返却機能
	    public String returnBookAndUpdateStatus(Long bookId) {
	        Optional<Book> optionalBook = bookRepository.findById(bookId);
	        if (optionalBook.isPresent()) {
	            Book book = optionalBook.get();
	            if ("BORROWED".equals(book.getStatus())) {
	                // 貸出中の場合の処理
	                book.setStatus("AVAILABLE");
	                bookRepository.save(book);

	                BorrowHistory borrowHistory = borrowHistoryRepository.findFirstByBookIdAndReturnDateIsNullOrderByBorrowDateDesc(bookId);
	                if (borrowHistory != null) {
	                    borrowHistory.setReturnDate(LocalDateTime.now());
	                    borrowHistoryRepository.save(borrowHistory);
	                    return "返却しました。";
	                } else {
	                    return "貸出履歴が見つかりません。";
	                }
	            } else {
	                return "書籍は貸出中ではありません。";
	            }
	        } else {
	            return "書籍が存在しません。";
	        }
	    }

}

	    
	   

	


	

