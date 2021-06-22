package de.tonline.model.api;

import lombok.Data;

/**
 * Address model based on the given api
 * see: http://jsonplaceholder.typicode.com/users/
 */
@Data
public class Address {
	private String street;
	private String suite;
	private String city;
	private String zipCode;
	private Geo geo;

}
