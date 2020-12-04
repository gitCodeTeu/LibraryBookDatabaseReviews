package ca.sheridancollege.controllers;


import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ca.sheridancollege.beans.Book;
import ca.sheridancollege.beans.Review;
import ca.sheridancollege.database.DatabaseAccess;

@Controller
public class HomeController {

	private DatabaseAccess db;

	public HomeController(DatabaseAccess db) {
		this.db = db;
	}

	@GetMapping("/")
	public String goHome(Model model) {
		List<Book> libBookList = db.getBookList();
		model.addAttribute("libBookList", libBookList);
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "/login";
	}

	@GetMapping("/logout")
	public String logout() {
		return "redirect:/";
	}

	@GetMapping("/addPage")
	public String goPage(Model model) {
		model.addAttribute("book", new Book());
		return "/admin/add-book";
	}

	/**
	 * adds a new book to the database,
	 * then sens user back to the index where the all books are listed 
	 * (indluding the added book)
	 * @param title of the book
	 * @param author name
	 * @param model 
	 * @return index
	 */
	@PostMapping("/addBook")
	public String addTheBook(
			@RequestParam(required = false) String title,
			@RequestParam(required = false) String author, 
			Model model) {
	

		//add book
		db.addBook(title, author);
		List<Book> libBookList = db.getBookList();
		model.addAttribute("libBookList", libBookList);
		return "index";
	}

	/**
	 * retrieves the list of reviews for a specific book
	 * then sends user to the view-books page 
	 * where the all reviews will be listed
	 * @param bookId unique id of the book
	 * @param model
	 * @return view-book
	 */
	@GetMapping("/viewBook/{bookId}")
	public String goViewBook(@PathVariable Long bookId, Model model) {

		Book book = db.getBook(bookId);
		model.addAttribute("book", book);

		List<Review> bookReviewList = db.getReviews(bookId);
		model.addAttribute("bookReviewList", bookReviewList);
		return "/view-book";
	}

	/**
	 * sends user to the add-review page 
	 * for a specific book (identified by the bookId)
	 * @param bookId unique id of the book
	 * @param model
	 * @return add-review page
	 */
	@GetMapping("/addReview/{bookId}")
	public String goAddReview(@PathVariable Long bookId, Model model) {
		Book book = db.getBook(bookId);
		model.addAttribute("book", book);
		return "/user/add-review";
	}

	/**
	 * sends user to the permission-denied page 
	 * @return permission-denied page
	 */
	@GetMapping("/permission-denied")
	public String permissionDenied() {
		return "/error/permission-denied";
	}


	/**
	 * adds the review for a specific book to the database, 
	 * then sends user back to the view-book page
	 * @param bookId unique id of the book
	 * @param review feeddback given by users
	 * @param model
	 * @return view-book
	 */
	@PostMapping("/submitReview/{bookId}")
	public String addReview(
			@PathVariable Long bookId, 
			@RequestParam(required = false) String review, Model model,
			@RequestParam String username) {
		db.addReview(bookId, review);
		
		Book book = db.getBook(bookId);
		model.addAttribute("book", book);

		List<Review> bookReviewList = db.getReviews(bookId);
		model.addAttribute("bookReviewList", bookReviewList);
		return "view-book";
	}

	/**
	 * sends user to the resgister page
	 * @return register page
	 */
	@GetMapping("/register")
	public String goRegisterPage() {
		return "register";
	}


	@Autowired
	private JdbcUserDetailsManager jdbcUserDetailsManager;

	@Autowired
	@Lazy
	private BCryptPasswordEncoder passEncoder;

	/**
	 * adds a new user with USER role to the database.
	 * 
	 * checks if username is already in the database,
	 * if it exist, sends user back to the register page and displays a message
	 * if not, it encrypts the password then add the user to the database 
	 * 
	 * @param username
	 * @param password
	 * @param model
	 * @return
	 */
	@PostMapping("/addUser")
	public String addUser(@RequestParam String username, 
			@RequestParam String password, Model model) {
		
		//check username
		Boolean userExist = jdbcUserDetailsManager.userExists(username);
		if (userExist == true) {
			model.addAttribute("message", "Username already in use!");
			return "register";
		}

		//add user
		List<GrantedAuthority> authorityList = new ArrayList<>();
		authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
		
		String encodedPassword = passEncoder.encode(password);
		User user = new User(username, encodedPassword, authorityList);

		jdbcUserDetailsManager.createUser(user);
		model.addAttribute("message", "Thanks for registering");

		List<Book> libBookList = db.getBookList();
		model.addAttribute("libBookList", libBookList);
		return "index";
	}

}
