package de.tonline.model.api;

import lombok.Data;

/**
 * Company model based on the given api
 * see: http://jsonplaceholder.typicode.com/users/
 */
@Data
public class Company {

	private String name;
	private String catchPhrase;
	private String bs;
}
