package com.elastic.mongo.app.service;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;

import com.elastic.mongo.app.entity.User;

public interface ElasticService {
	
	IndexResponse createElasticIndex(User user);
	
	List<Map<String, Object>> getUsers(String inputText);
	
	Map<String, Integer> prepareIndexing();

}
