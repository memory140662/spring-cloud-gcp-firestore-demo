package com.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.WriteResult;

@RestController
@RequestMapping("/user")
public class UserController {
	
	private static final String USER_PATH = "users";
	
	@Autowired
	Firestore firestore;
	
	@GetMapping
	public List<User> get() throws InterruptedException, ExecutionException{
		List<User> users = new ArrayList<User>();
		firestore.collection(USER_PATH).orderBy("createdAt", Direction.DESCENDING).get().get().forEach(snapshot -> {
			User user = (User) snapshot.toObject(User.class);
			user.setId(snapshot.getId());
			users.add(user);
		});
		return users;
	}
	
	@GetMapping("/{id}")
	public User get(@PathVariable String id) throws InterruptedException, ExecutionException {
		DocumentSnapshot snapshot = firestore.collection(USER_PATH).document(id).get().get();
		User user = (User) snapshot.toObject(User.class);
		user.setId(id);
		return user;
	}
	
	@PostMapping
	public User post(User user) throws InterruptedException, ExecutionException {
		user.setCreatedAt(new Date());
		DocumentReference reference = firestore.collection(USER_PATH).add(user).get();
		user.setId(reference.get().get().getId());
		return user;
	}
	
	@DeleteMapping("/{id}")
	public void delete(@PathVariable String id) throws InterruptedException, ExecutionException {
		WriteResult result = firestore.collection(USER_PATH).document(id).delete().get();
		System.out.println("User deleted time: " + new SimpleDateFormat("YYYY/MM/dd HH:mm:ss").format(result.getUpdateTime().toDate()));
	}
	
	
	@PutMapping("/{id}")
	public void update(@PathVariable String id, User user) throws InterruptedException, ExecutionException {
		DocumentReference reference = firestore.collection(USER_PATH).document(id);
		User origUser = reference.get().get().toObject(User.class);
		if (origUser != null) {
			user.setCreatedAt(origUser.getCreatedAt());
			WriteResult result = reference.set(user).get();
			System.out.println("User updated time: " + new SimpleDateFormat("YYYY/MM/dd HH:mm:ss").format(result.getUpdateTime().toDate()));
		}
	}
	
}
