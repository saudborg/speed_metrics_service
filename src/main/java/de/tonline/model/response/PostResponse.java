package de.tonline.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Custom response for the user.
 * Only the needed information is filtered
 */
@Data
@AllArgsConstructor
public class PostResponse {

	private String title;
	private String body;

}
