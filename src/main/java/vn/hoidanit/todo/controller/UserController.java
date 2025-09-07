package vn.hoidanit.todo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.todo.entity.ApiResponse;
import vn.hoidanit.todo.entity.User;
import vn.hoidanit.todo.service.UserService;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/users")
	public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) {
		User created = userService.createUser(user);
		var result = new ApiResponse<>(HttpStatus.CREATED, "createUser", created, null);
		// ApiResponse<User> result = new ApiResponse<User>(HttpStatus.CREATED,
		// "createUser", created, null)
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@GetMapping("/users")
	public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
		var result = new ApiResponse<>(HttpStatus.OK, "getAllUsers", userService.getAllUsers(), null);
		return ResponseEntity.ok().body(result);
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
		return userService.getUserById(id).map(user -> {
			var response = new ApiResponse<>(HttpStatus.OK, "getUserById", user, null);
			return ResponseEntity.ok(response);

		}).orElseGet(() -> {
			ApiResponse<User> errorResponse = new ApiResponse<>(HttpStatus.NOT_FOUND,
					"Không tìm thấy user với ID: " + id, null, "USER_NOT_FOUND");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
		});
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
		User updated = userService.updateUser(id, user);
		var result = new ApiResponse<>(HttpStatus.OK, "updateUser", updated, null);
		return ResponseEntity.ok(result);
	}

	// @ExceptionHandler(NoSuchElementException.class)
	// public ResponseEntity<ApiResponse<?>> handleNotFound(NoSuchElementException
	// ex) {
	// var result = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
	// "handleNotFound", null, ex.getMessage());
	// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
	// }

	@DeleteMapping("/users/{id}")
	public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		ApiResponse<User> result = new ApiResponse<User>(HttpStatus.NO_CONTENT, "deleteUser", null, null);
		return ResponseEntity.ok(result);
	}
}