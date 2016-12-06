package com.rest.service.resteasy;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.rest.service.utils.JsonMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Provider
public class JsonContextResolver implements ContextResolver<ObjectMapper> {

	final ObjectMapper mapper	= JsonMapper.nonEmptyMapper().getMapper();


	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}
}

