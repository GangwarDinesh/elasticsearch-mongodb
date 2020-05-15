package com.elastic.mongo.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elastic.mongo.app.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ElasticServiceImpl implements ElasticService {

	@Autowired
	private Client client;
	
	@Autowired
	private UserService userService;

	@SuppressWarnings("unchecked")
	@Override
	public IndexResponse createElasticIndex(User user) {
		IndexResponse indexResponse = null;

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> userObjectMap = mapper.convertValue(user, Map.class);

		IndexRequestBuilder builder[] = new IndexRequestBuilder[] {
				client.prepareIndex("users", "employee", String.valueOf(user.getId())).setSource(userObjectMap) };
		indexResponse = builder[0].get();

		return indexResponse;
	}
	
	@Override
	public List<Map<String, Object>> getUsers(String inputText){
		List<Map<String, Object>> response = new ArrayList<>();
		
		SearchResponse searchResponse = client.prepareSearch("users")
				.setTypes("employee")
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.wildcardQuery("name", "*"+inputText+"*"))
				.get();
		
		List<SearchHit> hitsList = Arrays.asList(searchResponse.getHits().getHits());
		hitsList.forEach(hits->{
			response.add(hits.getSourceAsMap());
		});
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Integer> prepareIndexing(){
		
		List<User> userList = userService.getUsers();

		ObjectMapper mapper = new ObjectMapper();
		List<IndexRequestBuilder> indexRequestBuilders = new ArrayList<>();
		Map<String, Integer> result = new HashMap<>();
		int successCount = 0;
		int failedCount = 0;
		for(int count = 0; count< userList.size(); count++) {
			User user = userList.get(count);
			Map<String, Object> userObjectMap = mapper.convertValue(user, Map.class);
			try {
				indexRequestBuilders.add(client.prepareIndex("users", "employee", String.valueOf(user.getId())).setSource(userObjectMap));
				successCount = successCount +indexRequestBuilders.get(count).get().getShardInfo().getSuccessful(); 
				failedCount = failedCount + indexRequestBuilders.get(count).get().getShardInfo().getFailed();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		result.put("total_count", successCount+failedCount);
		result.put("success_count", successCount);
		result.put("failed_count", failedCount);
		
		return result;
	}
}
