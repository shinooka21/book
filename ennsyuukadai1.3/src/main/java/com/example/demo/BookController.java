package com.example.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BookController {
	
	@Autowired
	BookRepository bookRepository;
	
	 @Autowired
	    private BookService bookService;
	
	 //データベースへ保存するためのメソッド
    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }
	
	//必要な機能
	//書籍の検索機能、書籍の貸出・返却機能、貸出履歴の確認
	
        //初期画面を返すためのメソッド
    @RequestMapping("/")
    public ModelAndView showAllBooks(ModelAndView mv) {
        List<Book> allBooks = bookService.getAllBooks();
        mv.addObject("books", allBooks);
        mv.setViewName("index"); 
        return mv;
	    }
	
	//新規登録画面へ移行するためのメソッド
    @RequestMapping(value = "/add")
    public ModelAndView add(ModelAndView mv) {
    mv.setViewName("book_add");
    return mv;
    }
    
    //新規登録をするためのメソッド
    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    public ModelAndView insert(
    		@RequestParam(name = "title") String title,
    		@RequestParam(name = "author") String author ,
    		@RequestParam(name = "isbn") String isbn,
    		ModelAndView mv) {
        bookService.insertBook(title, author, isbn);
        mv.addObject("books",bookService.getAllBooks());
        mv.setViewName("index"); 
        return mv;
	    }
    
 // 書籍の編集画面へ移行するためのメソッド
    @GetMapping("/edit")
    public ModelAndView showEditBookForm(@RequestParam(name = "id") Long id) {
        // IDに基づいて書籍を取得
        Book book = bookService.getBookById(id);
        ModelAndView mv = new ModelAndView();
        mv.addObject("book", book);
        mv.addObject("originalStatus", book.getStatus());
        mv.setViewName("edit-book-form");
        return mv;
    }

    // 編集内容を保存し、indexページにリダイレクトするメソッド
    @PostMapping("/update/{id}")
    public ModelAndView updateBook(@ModelAttribute Book updatedBook,
                                   @RequestParam("originalStatus") String originalStatus,
                                   ModelAndView mv) {
        updatedBook.setStatus(originalStatus);
        bookService.updateBook(updatedBook);
        // 更新後の本のリストを取得
        List<Book> bookList = bookService.getAllBooks();
        mv.addObject("books", bookList);
        mv.setViewName("index");
        
        return mv;
    }
    
    //削除確認画面を表示させるためのメソッド
    @GetMapping("/delete")
    public ModelAndView showDeleteBookConfirmation(@RequestParam(name = "id") Long id) {
        // 書籍情報を取得するなどの処理が可能
        Book deletedBook = bookService.getBookById(id);

        ModelAndView mv = new ModelAndView();
        mv.addObject("book", deletedBook);
        mv.setViewName("delete-book-confirmation");
        
        return mv;
    }
 // 書籍を削除し、更新後のトップページを表示するメソッド
    @PostMapping("/delete/{id}")
    public ModelAndView processDeleteConfirmation(@PathVariable("id") Long id) {
        // IDに基づいて書籍を削除
        bookService.deleteBookById(id);

        // 更新後の書籍リストを取得
        List<Book> updatedBookList = bookService.getAllBooks();
        ModelAndView mv = new ModelAndView();
        mv.addObject("books", updatedBookList);
        mv.setViewName("index");

        return mv;
    }


    
    //書籍の検索結果を表示するメソッド
    // 検索結果を表示するメソッド
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView searchBooks(@RequestParam(name = "id") String id) {
        ModelAndView mv = new ModelAndView();

        try {
            List<Book> searchResult = bookService.searchBooks(id);
            mv.addObject("books", searchResult);
        } catch (IllegalArgumentException e) {
            mv.addObject("error", e.getMessage());
        }
        mv.setViewName("search");
        return mv;
    }

    
    
  //履歴確認画面へ移行ためのメソッド
    @GetMapping("/history")
    public ModelAndView getBorrowHistory(@RequestParam Long bookId) {
        List<BorrowHistory> historyList = bookService.getBorrowHistory(bookId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("borrow_history");
        modelAndView.addObject("historyList", historyList);
        return modelAndView;
    }
    
    @GetMapping("/borrow-history/{id}")
    public ModelAndView viewBorrowHistory(@PathVariable(name = "id") Long bookId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("borrow_history");

        // 貸出履歴を取得してビューに追加
        List<BorrowHistory> historyList = bookService.getBorrowHistory(bookId);
        modelAndView.addObject("historyList", historyList);

        return modelAndView;
    }


 
    //貸出確認画面を表示するためのメソッド
    
    
    //借りるボタンを押しトップページへ移行するためのメソッド
    @PostMapping("/lend/{id}")
    public ModelAndView lendBook(@PathVariable(name = "id") Long bookId) {
        String lendResult = bookService.lendBookAndUpdateStatus(bookId);
        ModelAndView modelAndView = bookService.getBookListView();
        modelAndView.addObject("lendResult", lendResult);
        return modelAndView;
    }
    //★★上記借りるボタンのメソッドはトップページのみで完結するようには機能している※要確認
    
    
    //返却ボタンを押すとステータスが変わるメソッド定義
    @PostMapping("/return/{id}")
    public ModelAndView returnBook(@PathVariable(name = "id") Long bookId) {
        String returnResult = bookService.returnBookAndUpdateStatus(bookId);
        ModelAndView modelAndView = bookService.getBookListView();
        modelAndView.addObject("returnResult", returnResult);
        return modelAndView;
    }

}    


