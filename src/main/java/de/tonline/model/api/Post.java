package de.tonline.model.api;

import lombok.Data;

/**
 * Simple post model based on the given api
 * see: http://jsonplaceholder.typicode.com/posts
 */
@Data
public class Post {

	private long userId;
	private long id;
	private String title;
	private String body;

}
