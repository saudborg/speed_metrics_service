package de.tonline.controller;

import de.tonline.exception.ServerException;
import de.tonline.exception.UserNotFoundException;
import de.tonline.model.api.Post;
import de.tonline.model.api.User;
import de.tonline.model.response.PostResponse;
import de.tonline.model.response.UserPostResponse;
import de.tonline.service.RequestService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Main controller which contain the endpoint for fetch the data async
 */
@RestController
public class PostController {

	private RequestService requestService;

	@Autowired
	public PostController(final RequestService requestService) {
		this.requestService = requestService;
	}

	@ApiOperation(value = "get posts by user", notes = "Fetch async the given user data along with all the posts")
	@ApiResponses(
			value = { @ApiResponse(code = 200, message = "Successful retrieval", response = UserPostResponse.class) })
	@RequestMapping(value = "/posts/{userId}", method = RequestMethod.GET)
	public ResponseEntity<UserPostResponse> getPostsByUser(@PathVariable final String userId)
			throws InterruptedException, ExecutionException, UserNotFoundException, ServerException {
		CompletableFuture<Post[]> posts = requestService.fetchPostsByUserId(userId);
		CompletableFuture<User> user = requestService.fetchUserById(userId);

		CompletableFuture.allOf(posts, user).join();

		UserPostResponse response = buildUserPostResponse(posts, user);
		return ResponseEntity.ok(response);
	}

	/**
	 * Given a future Post and User, do a simple merge to the new class UserPostResponse.
	 * For the posts, only keeping the body and title information
	 *
	 * @param posts
	 * @param user
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private UserPostResponse buildUserPostResponse(final CompletableFuture<Post[]> posts,
			final CompletableFuture<User> user) throws InterruptedException, ExecutionException {
		UserPostResponse response = new UserPostResponse();
		List<PostResponse> postResponse = new ArrayList<>();

		Arrays.stream(posts.get()).forEach(post -> postResponse.add(new PostResponse(post.getTitle(), post.getBody())));

		response.setPosts(postResponse);
		response.setUser(user.get());
		return response;
	}
}
