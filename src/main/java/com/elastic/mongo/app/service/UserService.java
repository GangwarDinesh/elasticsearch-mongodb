package com.elastic.mongo.app.service;

import java.util.List;

import com.elastic.mongo.app.entity.User;

public interface UserService {
	
	void createUser(User user);
	
	 List<User> getUsers();

}
