package com.mirakl.hybris.core.utils.assertions;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

public class MiraklExtractedFieldsAssert {

  private final List<?> objects;
  private final String[] fieldNames;

  public MiraklExtractedFieldsAssert(List<?> objects, String... fieldNames) {
    this.objects = objects;
    this.fieldNames = fieldNames;
  }

  public void containsExactly(Tuple... tuples) {
    int fieldCount = fieldNames.length;
    for (Tuple tuple : tuples) {
      assertThat(tuple.getFieldCount()).isEqualTo(fieldCount);
    }
    for (int i = 0; i < fieldCount; i++) {
      List<Object> propertyValues = new ArrayList<>();
      for (Tuple tuple : tuples) {
        propertyValues.add(tuple.getFields().get(i));
      }
      assertThat(objects).onProperty(fieldNames[i]).containsExactly(propertyValues.toArray());
    }
  }

}
