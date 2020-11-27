package ca.sheridancollege.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
	private Long id;
	private Long bookId;
	private String text = "";
}
