package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;

public class MapperService {

    public static JsonNode getJsonNode(final HttpResponse<String> stringHttpResponseLocation) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(stringHttpResponseLocation.body());
    }


}
