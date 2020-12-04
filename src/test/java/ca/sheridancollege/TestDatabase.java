package ca.sheridancollege;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import ca.sheridancollege.database.DatabaseAccess;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TestDatabase {
	
	@Autowired
	private DatabaseAccess db;
	
	@Autowired
	MockMvc mockMvc;
	
	
	/**
	 * tests whether the new book adds to database
	 */
	@Test
	public void testDatabaseAddBook() {
		int origsize = db.getBookList().size();
		db.addBook("Book1", "Mr. Author");
		int newSize = db.getBookList().size();
		assertThat(newSize).isEqualTo(origsize+1);
	}
	
	/**
	 * tests whether the new review adds to database
	 */
	@Test
	public void testDatabaseAddReview() {
		
		System.out.println(db.getBook(1L));

		int origsize = db.getReviews(1L).size();
		db.addReview(1L, "lorem ipssum2");
		
		int newSize = db.getReviews(1L).size();
		assertThat(newSize).isEqualTo(origsize+1);
	}
	
	/**
	 * tests whether the root works
	 * @throws Exception
	 */
	@Test
	public void testRoot() throws Exception{
		mockMvc.perform(get("/"))
		.andExpect(status().isOk())
		.andExpect(view().name("index"));
	}

	/**
	 * tests whether addBook method works
	 * @throws Exception
	 */
	@Test
	public void testAddBook() throws Exception{
		LinkedMultiValueMap<String, String> requestParams = 
				new LinkedMultiValueMap<>();
		
		requestParams.add("title", "Book1");
		requestParams.add("author", "Smith");
		
		mockMvc.perform(post("/addBook")
				.params(requestParams))
				.andExpect(status().isOk())
				.andExpect(view().name("index"));
	}

	/**
	 * tests whether login method works
	 * @throws Exception
	 */
	@Test
	public void testLogin() throws Exception{
		LinkedMultiValueMap<String, String> requestParams = 
				new LinkedMultiValueMap<>();
		
		requestParams.add("username", "bugs");
		requestParams.add("password", "bunny");
		
		mockMvc.perform(get("/login")
				.params(requestParams))
				.andExpect(status().isOk());
	}
}
