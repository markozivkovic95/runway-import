package com.example.runwayimport.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.runwayimport.models.JobDTO;
import com.example.runwayimport.models.SearchParamsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JobsService {
    
    private static final String SEARCH_ENDPOINT = "https://elc.brandmaker.com/dse/rest/v1.0/jobs/_search";
    private static final String GET_JOB_BY_ID = "https://elc.brandmaker.com/dse/rest/v1.0/dse-object/%s/0";

    private static final String VALUES_KEY = "values";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public JobsService(final RestTemplateBuilder restTemplateBuilder, final ObjectMapper objectMapper) {

        this.restTemplate = restTemplateBuilder.basicAuthentication("filip.djordjevic", "EsteeLauder1")
                .build();
        this.objectMapper = objectMapper;
    }

    public List<JobDTO> findAllJobs() {
        
        final String filterId = "-3";

        final Map<Object, Object> response = this.restTemplate.postForEntity(
            SEARCH_ENDPOINT,
            new SearchParamsDTO(filterId),
            HashMap.class
        ).getBody();

        final Object jobs;

        if (response != null) {
            jobs = response.get(VALUES_KEY);
        } else {
            jobs = null;
        }

        return this.objectMapper.convertValue(
                jobs,
                TypeFactory.defaultInstance().constructCollectionType(List.class, JobDTO.class)
        );
    }

    public JobDTO getJobById(final String id) {

        final Map<Object, Object> responseBody = this.restTemplate.getForEntity(String.format(GET_JOB_BY_ID, id), HashMap.class)
                .getBody();
        
        if (responseBody == null) {
            return null;
        }
        
        return this.objectMapper.convertValue(responseBody, JobDTO.class);
    }

}
