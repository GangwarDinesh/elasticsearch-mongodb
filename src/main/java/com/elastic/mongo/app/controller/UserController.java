package com.elastic.mongo.app.controller;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elastic.mongo.app.entity.User;
import com.elastic.mongo.app.service.ElasticService;
import com.elastic.mongo.app.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ElasticService elasticService;
	
	@PostMapping("/addUser")
	public ResponseEntity<?> createUser(@RequestBody User user){
	
		userService.createUser(user);
		IndexResponse indexResponse  = elasticService.createElasticIndex(user);
		ResponseEntity<IndexResponse> responseEntity = new ResponseEntity<>(indexResponse, new HttpHeaders(), HttpStatus.OK);
		return responseEntity;
	}
	
	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> search(@RequestParam("inputText") String inputText){	
		List<Map<String, Object>> responseList = elasticService.getUsers(inputText);
		responseList = responseList.stream().sorted(Comparator.comparing(o-> String.valueOf(o.get("id")))).collect(Collectors.toList());
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("timestamp", LocalDateTime.now());
		responseMap.put("response", responseList);
		if(null != responseList && !responseList.isEmpty()) {
			responseMap.put("status", HttpStatus.FOUND);
		}else {
			responseMap.put("status", HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Map<String, Object>>(responseMap, new HttpHeaders(), HttpStatus.OK);
	}
	@GetMapping(value = "/createAllIndexing", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> prepareIndexing(){	
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("timestamp", LocalDateTime.now());
		
		Map<String, Integer> result = elasticService.prepareIndexing();
		if(null != result && !result.isEmpty()) {
			responseMap.put("status", HttpStatus.OK);
			responseMap.put("message", "All indexing have been done.");
			responseMap.put("reponse", result);
		}else {
			responseMap.put("status", HttpStatus.EXPECTATION_FAILED);
			responseMap.put("message", "Technical error occurred.");
			responseMap.put("reponse", "{}");
		}
		return new ResponseEntity<Map<String, Object>>(responseMap, new HttpHeaders(), HttpStatus.OK);
	}
}
