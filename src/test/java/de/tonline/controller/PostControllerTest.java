package de.tonline.controller;

import de.tonline.exception.ServerException;
import de.tonline.exception.UserNotFoundException;
import de.tonline.model.response.UserPostResponse;
import de.tonline.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PostControllerTest {
	private PostController postController;

	@BeforeEach
	public void before() {
		RequestService requestService = new RequestService("http://jsonplaceholder.typicode.com/", new RestTemplate());
		postController = new PostController(requestService);
	}

	@Test
	public void test_responseUser1()
			throws InterruptedException, ExecutionException, UserNotFoundException, ServerException {
		ResponseEntity<UserPostResponse> postsByUser = postController.getPostsByUser("1");
		assertNotNull(postsByUser);
		assertEquals(postsByUser.getStatusCode(), HttpStatus.OK);
		assertNotNull(postsByUser.getBody());
		assertNotNull(postsByUser.getBody().getUser());
		assertNotNull(postsByUser.getBody().getPosts());
	}

	@Test
	public void test_responseUser10()
			throws InterruptedException, ExecutionException, UserNotFoundException, ServerException {
		ResponseEntity<UserPostResponse> postsByUser = postController.getPostsByUser("10");
		assertNotNull(postsByUser);
		assertEquals(postsByUser.getStatusCode(), HttpStatus.OK);
		assertNotNull(postsByUser.getBody());
		assertNotNull(postsByUser.getBody().getUser());
		assertNotNull(postsByUser.getBody().getPosts());
		postsByUser.getBody().getPosts().forEach(post -> {
			assertNotNull(post.getBody());
			assertNotNull(post.getTitle());
		});
	}

	@Test
	public void test_responseUser33_doesnt_exists() {
		assertThrows(UserNotFoundException.class, () -> postController.getPostsByUser("33"));
	}

	@Test
	public void test_responseUserString() {
		assertThrows(UserNotFoundException.class, () -> postController.getPostsByUser("anyValueNotLong"));
	}
}