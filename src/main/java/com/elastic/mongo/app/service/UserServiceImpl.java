package com.elastic.mongo.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elastic.mongo.app.entity.User;
import com.elastic.mongo.app.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void createUser(User user) {
		User u = userRepository.save(user);
		System.out.println(u);
	}
	
	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

}
