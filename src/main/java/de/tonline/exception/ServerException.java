package de.tonline.exception;

/**
 * Generic exception which should be handle by ControllerAdvisor wherever we have an know exception
 */
public class ServerException extends Exception {

	public ServerException(final Exception e) {
		super(e);
	}
}
