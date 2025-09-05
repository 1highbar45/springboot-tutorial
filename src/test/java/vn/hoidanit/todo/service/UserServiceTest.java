package vn.hoidanit.todo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.hoidanit.todo.entity.User;
import vn.hoidanit.todo.repository.UserRepository;
import vn.hoidanit.todo.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	public void createUser_shouldReturnUser_WhenEmailValid() {
		// arrange
		User inputUser = new User(null, "eric", "hoidanit@gmail.com");
		User outputUser = new User(1L, "eric", "hoidanit@gmail.com");

		when(this.userRepository.existsByEmail(inputUser.getEmail())).thenReturn(false);

		when(this.userRepository.save(any())).thenReturn(outputUser);

		// act
		User result = this.userService.createUser(inputUser);

		// assert
		assertEquals(1L, result.getId());
	}

	@Test
	public void createUser_shouldThrowException_WhenEmailInvalid() {
		// arrange
		User inputUser = new User(null, "eric", "hoidanit@gmail.com");

		when(this.userRepository.existsByEmail(inputUser.getEmail())).thenReturn(true);

		// act
		Exception ex = assertThrows(IllegalArgumentException.class, () -> {
			this.userService.createUser(inputUser);
		});

		// assert
		assertEquals("Email already exists", ex.getMessage());
	}

	@Test
	public void getAllUsers_shouldReturnAllUsers() {
		// arrange
		List<User> outputUsers = new ArrayList<>();
		outputUsers.add(new User(1L, "alice", "alice@gmail.com"));
		outputUsers.add(new User(1L, "bob", "bob@gmail.com"));

		when(this.userRepository.findAll()).thenReturn(outputUsers);

		// act
		List<User> result = this.userService.getAllUsers();

		// assert
		assertEquals(2, result.size());
		assertEquals("alice@gmail.com", result.get(0).getEmail());
	}

	@Test
	public void getUserById_shouldReturnOptionalUser() {
		// arrange
		Long inputId = 1L;
		User inputUser = new User(1L, "eric", "eric@gmail.com");
		Optional<User> userOptionalOutput = Optional.of(inputUser);

		when(this.userRepository.findById(inputId)).thenReturn(userOptionalOutput);

		// act
		Optional<User> result = this.userService.getUserById(inputId);

		// assert
		assertEquals(true, result.isPresent());
	}

	@Test
	public void deleteUser_ShouldReturnVoid_WhenUserExist() {
		// arrange
		Long inputId = 1L;
		when(this.userRepository.existsById(inputId)).thenReturn(true);

		// act
		this.userService.deleteUser(inputId);

		// assert
		verify(this.userRepository).deleteById(inputId);
	}

	@Test
	public void deleteUser_ShouldReturnException_WhenUserNotExist() {
		// arrange
		Long inputId = 1L;
		when(this.userRepository.existsById(inputId)).thenReturn(false);

		// act
		Exception ex = assertThrows(NoSuchElementException.class, () -> {
			this.userService.deleteUser(inputId);
		});

		// assert
		assertEquals("User not found", ex.getMessage());
	}

	@Test
	public void updateUser_shouldReturnUser_whenValid() {
		// arrange
		Long inputId = 1L;
		User inputUser = new User(1L, "old name", "old@gmail.com");
		User outputUser = new User(1L, "new name", "new@gmail.com");

		when(this.userRepository.findById(inputId)).thenReturn(Optional.of(inputUser));
		when(this.userRepository.save(any())).thenReturn(outputUser);

		// act
		User result = this.userService.updateUser(inputId, inputUser);

		// assert
		assertEquals("new name", result.getName());
	}
}
