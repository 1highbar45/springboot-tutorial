package vn.hoidanit.todo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.hoidanit.todo.IntegrationTest;
import vn.hoidanit.todo.entity.User;
import vn.hoidanit.todo.repository.UserRepository;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void init() {
		this.userRepository.deleteAll();
	}

	@Test
	public void createUser_shouldReturnUser_whenValid() throws Exception {
		// arrange
		User inputUser = new User(null, "hoidanit IT", "hoidanit@gmail.com");

		// action
		String resultStr = mockMvc
				.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(inputUser)))
				.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

		// assert
		System.out.println("resultStr: " + resultStr);
		User outputUser = objectMapper.readValue(resultStr, User.class);

		assertEquals(inputUser.getName(), outputUser.getName());
	}

	@Test
	public void getAllUsers() throws Exception {
		// arrange
		User user1 = new User(null, "name1", "name1@gmail.com");
		User user2 = new User(null, "name2", "name2@gmail.com");

		List<User> data = List.of(user1, user2);

		this.userRepository.saveAll(data);

		// action
		String resultStr = this.mockMvc.perform(get("/users")).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString();

		List<User> result = this.objectMapper.readValue(resultStr, new TypeReference<List<User>>() {
		});

		// assert
		assertEquals(2, result.size());
		assertEquals("name1@gmail.com", result.get(0).getEmail());
	}

	@Test
	public void getUserById() throws Exception {
		// arrange
		User user = new User(null, "name-get-by-id", "name1@gmail.com");

		User userInput = this.userRepository.saveAndFlush(user);

		// action
		String resultStr = this.mockMvc.perform(get("/users/{id}", userInput.getId())).andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		User userOutput = this.objectMapper.readValue(resultStr, User.class);

		// assert
		assertEquals("name-get-by-id", userOutput.getName());
	}

	@Test
	public void getUserById_shouldEmpty_whenIdNotFound() throws Exception {
		// arrange

		// action
		this.mockMvc.perform(get("/users/{id}", 0)).andExpect(status().isNotFound());

		// assert
	}

	@Test
	public void updateUser() throws Exception {
		// arrange
		User user = new User(null, "old-name", "old@gmail.com");
		User userInput = this.userRepository.saveAndFlush(user);

		User updateUser = new User(userInput.getId(), "new-name", "new@gmail.com");

		// action
		String resultStr = mockMvc
				.perform(put("/users/{id}", userInput.getId()).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsBytes(updateUser)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		User userOutput = objectMapper.readValue(resultStr, User.class);

		// assert
		assertEquals("new-name", userOutput.getName());
	}

	@Test
	public void deleteUser() throws Exception {
		// arrange
		User user = new User(null, "delete-name", "delete@gmail.com");
		User userInput = this.userRepository.saveAndFlush(user);

		// action
		this.mockMvc.perform(delete("/users/{id}", userInput.getId()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		// assert
		long countDb = this.userRepository.count();
		assertEquals(0, countDb);
	}
}
