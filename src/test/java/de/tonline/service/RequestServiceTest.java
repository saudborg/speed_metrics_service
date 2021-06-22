package de.tonline.service;

import de.tonline.exception.ServerException;
import de.tonline.exception.UserNotFoundException;
import de.tonline.model.api.Post;
import de.tonline.model.api.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

	private RequestService requestService;

	@Mock
	private RestTemplate restTemplateMock;

	@BeforeEach
	public void before() {
		String mockUrl = "anyValue";
		requestService = new RequestService(mockUrl, restTemplateMock);
	}

	@Test
	public void test_fetchUserById()
			throws UserNotFoundException, ExecutionException, InterruptedException, ServerException {
		final User mockUser = new User();
		mockUser.setName("MockUser");
		mockUser.setId(1);
		Mockito.when(restTemplateMock.getForObject(anyString(), eq(User.class))).thenReturn(mockUser);

		CompletableFuture<User> userCompletableFuture = requestService.fetchUserById("1");
		User user = userCompletableFuture.get();
		assertNotNull(user);
		assertEquals(user.getId(), mockUser.getId());
	}

	@Test
	public void test_fetchUnknownUserById() {
		Mockito.when(restTemplateMock.getForObject(anyString(), eq(User.class)))
				.thenThrow(HttpClientErrorException.NotFound.class);

		assertThrows(UserNotFoundException.class, () -> requestService.fetchUserById("1"));
	}

	@Test
	public void test_UnexcitedResponseFromAPI() {
		Mockito.when(restTemplateMock.getForObject(anyString(), eq(User.class)))
				.thenThrow(HttpClientErrorException.BadRequest.class);

		assertThrows(ServerException.class, () -> requestService.fetchUserById("1"));
	}

	@Test
	public void test_fetchPostsByUserId()
			throws ExecutionException, InterruptedException, ServerException {
		final Post mockPost = new Post();
		mockPost.setUserId(1);
		mockPost.setId(1);
		mockPost.setTitle("Title");
		mockPost.setBody("Body");
		final Post[] postResponse = new Post[1];
		postResponse[0] = mockPost;
		Mockito.when(restTemplateMock.getForObject(anyString(), eq(Post[].class), anyMap())).thenReturn(postResponse);

		CompletableFuture<Post[]> postFuture = requestService.fetchPostsByUserId("1");
		Post[] posts = postFuture.get();
		assertNotNull(posts);
	}

	@Test
	public void test_fetchPostsByUserIdBadRequest() {
		Mockito.when(restTemplateMock.getForObject(anyString(), eq(Post[].class), anyMap()))
				.thenThrow(HttpClientErrorException.BadRequest.class);

		assertThrows(ServerException.class, () -> requestService.fetchPostsByUserId("1"));
	}
}