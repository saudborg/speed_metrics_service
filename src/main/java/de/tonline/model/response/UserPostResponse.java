package de.tonline.model.response;

import de.tonline.model.api.User;
import lombok.Data;

import java.util.List;

/**
 * Custom model response to be returned to the api
 */
@Data
public class UserPostResponse {

	private User user;
	private List<PostResponse> posts;

}
