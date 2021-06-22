package de.tonline.exception;

/**
 * Exception class which is thrown when an user can not be found when fetching the user endpoint
 */
public class UserNotFoundException extends Exception {

	public UserNotFoundException(final String id) {
		super("User " + id + " not found");
	}
}
