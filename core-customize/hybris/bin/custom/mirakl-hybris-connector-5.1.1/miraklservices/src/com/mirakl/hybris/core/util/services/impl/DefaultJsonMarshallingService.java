package com.mirakl.hybris.core.util.services.impl;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import shaded.com.fasterxml.jackson.core.type.TypeReference;
import shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultJsonMarshallingService implements JsonMarshallingService {

  protected ObjectMapper mapper;

  @Override
  public <T> T fromJson(String objectToUnmarshal, Class<T> type) {
    try {
      if (objectToUnmarshal != null) {
        return mapper.reader(type).readValue(objectToUnmarshal);
      }
      return null;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T fromJson(String objectToUnmarshal, TypeReference<?> typeReference) {
    try {
      if (objectToUnmarshal != null) {
        return mapper.reader(typeReference).readValue(objectToUnmarshal);
      }
      return null;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> String toJson(T objectToMarshal) {
    try {
      return mapper.writeValueAsString(objectToMarshal);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public <T> String toJson(T objectToMarshal, TypeReference<?> typeReference) {
    try {
      return mapper.writerWithType(typeReference).writeValueAsString(objectToMarshal);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String prettify(String json) {
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fromJson(json, Object.class));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Required
  public void setMapper(ObjectMapper mapper) {
    this.mapper = mapper;
  }
}
