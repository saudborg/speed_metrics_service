package de.tonline.model.api;

import lombok.Data;

/**
 * Geo Location model based on the given api
 * see: http://jsonplaceholder.typicode.com/users/
 */
@Data
public class Geo {
	private String lat;
	private String lng;
}
