package com.mirakl.hybris.core.utils.assertions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Tuple {

  private List<Object> fields;
  private int fieldCount;

  public Tuple(Object... fields) {
    this.fields = Arrays.asList(fields);
    fieldCount = this.fields.size();
  }

  public static Tuple tuple(Object... fields) {
    return new Tuple(fields);
  }

  public List<Object> getFields() {
    return fields;
  }

  public Integer getFieldCount() {
    return fieldCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Tuple tuple = (Tuple) o;
    return Objects.equals(fields, tuple.fields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fields);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Tuple[");
    boolean first = true;
    for (Object field : fields) {
      if (!first) {
        builder.append(',');
      } else {
        first = false;
      }
      builder.append(field);
    }
    builder.append(']');
    return builder.toString();
  }
}
