package de.tonline.model.api;

import lombok.Data;

/**
 * User model based on the given api
 * see: http://jsonplaceholder.typicode.com/users/
 */
@Data
public class User {

	private long id;
	private String name;
	private String username;
	private String email;
	private Address address;
	private String phone;
	private String website;
	private Company company;

}
