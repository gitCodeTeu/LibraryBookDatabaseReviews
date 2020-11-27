package ca.sheridancollege.beans;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
	private Long id;
	private String title = "";
	private String author = "";
}
