package ca.sheridancollege.database;

import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ca.sheridancollege.beans.Book;
import ca.sheridancollege.beans.Review;

@Repository
public class DatabaseAccess {

	private NamedParameterJdbcTemplate jdbc;
	
	
	public DatabaseAccess(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * retrieves all the books
	 * @return Lists<Book>
	 */
	public List<Book> getBookList() {
		MapSqlParameterSource namedParam = new MapSqlParameterSource();
		String sql = "SELECT * FROM books";
		BeanPropertyRowMapper<Book> booksMapper = new BeanPropertyRowMapper<Book>(Book.class);

		List<Book> booksList = jdbc.query(sql, namedParam, booksMapper);
		return booksList;
	}

	/**
	 * retrieves a book with the specified id 
	 * @param id
	 * @return
	 */
	public Book getBook(Long id) {
		MapSqlParameterSource namedParam = new MapSqlParameterSource();
		String sql = "SELECT * FROM books WHERE id=:id";
		namedParam.addValue("id", id);
		BeanPropertyRowMapper<Book> booksMapper = new BeanPropertyRowMapper<Book>(Book.class);

		List<Book> booksList = jdbc.query(sql, namedParam, booksMapper);
		return booksList.isEmpty()?  null : booksList.get(0);
	}

	/**
	 * retrieves all reviews for a specific book
	 * @param bookId
	 * @return
	 */
	public List<Review> getReviews(Long bookId){
		MapSqlParameterSource namedParam = new MapSqlParameterSource();
		String sql = "SELECT text FROM reviews WHERE bookId=:bookId";
		namedParam.addValue("bookId", bookId);
		BeanPropertyRowMapper<Review> booksMapper = 
				new BeanPropertyRowMapper<Review>(Review.class);
		
		List<Review> bookReviewList = jdbc.query(sql, namedParam, booksMapper);
		return bookReviewList.isEmpty()? null : bookReviewList;
	}
	
	/**
	 * adds a new book in the database
	 * @param title of the book
	 * @param author name
	 */
	public void addBook(String title, String author) {
		MapSqlParameterSource namedParam = new MapSqlParameterSource();
		String sql = "INSERT INTO books (title,author) VALUES (:title,:author)";
		namedParam.addValue("title", title);
		namedParam.addValue("author", author);
		jdbc.update(sql, namedParam);
	}
	
	/**
	 * adds a new review in the database
	 * @param bookId unique id of the book
	 * @param review info given by user
	 */
	public void addReview(Long bookId, String review) {
		MapSqlParameterSource namedParam = new MapSqlParameterSource();
		String sql = "INSERT INTO reviews (bookId,text) VALUES (:bookId,:review)";
		namedParam.addValue("bookId", bookId);
		namedParam.addValue("review", review);

		jdbc.update(sql, namedParam);
		
	}
	
	/**
	 * adds a new user in the database
	 * @param username musts be unique
	 * @param password
	 * @param role authorization level
	 */
	public void addUser(String username, String password, String role) {
		MapSqlParameterSource namedParam = new MapSqlParameterSource();
		String sql = "INSERT INTO users (username, password, enabled) "
				+ "VALUES (:username, :password, :enabled)";
		namedParam.addValue("username", username);
		namedParam.addValue("password", password);
		namedParam.addValue("enabled", true);

		jdbc.update(sql, namedParam);
		
		sql = "INSERT INTO authorities (username, role) "
				+ "VALUES (:username, :role)";
		namedParam.addValue("username", username);
		namedParam.addValue("role", role);
	}
	
}
