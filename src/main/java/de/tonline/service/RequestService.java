package de.tonline.service;

import de.tonline.configuration.AsyncConfiguration;
import de.tonline.exception.ServerException;
import de.tonline.exception.UserNotFoundException;
import de.tonline.model.api.Post;
import de.tonline.model.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Class containing the logic for request async multiple API and provide Future data
 */
@Service
public class RequestService {

	private final String apiUrl;
	private final RestTemplate restTemplate;

	@Autowired
	public RequestService(@Value("${api.url}") final String apiUrl, @Autowired final RestTemplate restTemplate) {
		this.apiUrl = apiUrl;
		this.restTemplate = restTemplate;
	}

	/**
	 * Fetch the given API , request user endpoint, passing his ID to get the full user information.
	 *
	 * @param userId
	 * @return CompletableFuture object with the User information
	 * @throws UserNotFoundException
	 * @throws ServerException
	 */
	@Async(AsyncConfiguration.COMPONENT_ID)
	public CompletableFuture<User> fetchUserById(final String userId) throws UserNotFoundException, ServerException {
		try {
			User user = restTemplate.getForObject(apiUrl + "users/" + userId, User.class);
			return CompletableFuture.completedFuture(user);
		} catch (HttpClientErrorException.NotFound e) {
			throw new UserNotFoundException(userId);
		} catch (Exception e) {
			throw new ServerException(e);
		}
	}

	/**
	 * Fetch the given API , request post endpoint, passing an user ID to get a list of posts from this user.
	 *
	 * @param userId
	 * @return CompletableFuture object with the User information
	 * @throws ServerException
	 */
	@Async(AsyncConfiguration.COMPONENT_ID)
	public CompletableFuture<Post[]> fetchPostsByUserId(final String userId) throws ServerException {
		try {
			final Map<String, Object> mapRequest = new HashMap<>();
			mapRequest.put("userId", userId);

			Post[] postResponse = restTemplate.getForObject(apiUrl + "posts?userId={userId}", Post[].class, mapRequest);
			return CompletableFuture.completedFuture(postResponse);
		} catch (Exception e) {
			throw new ServerException(e);
		}

	}
}
