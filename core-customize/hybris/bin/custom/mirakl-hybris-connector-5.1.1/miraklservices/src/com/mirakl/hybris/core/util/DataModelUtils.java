package com.mirakl.hybris.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

public class DataModelUtils {

  private DataModelUtils() {}

  public static <T extends ItemModel> Set<PK> extractPks(Collection<T> items) {

    Collection<PK> pks = Collections2.transform(items, new Function<T, PK>() {
      @Override
      public PK apply(T item) {
        return item.getPk();
      }
    });
    return ImmutableSet.copyOf(pks);
  }

  public static Set<String> extractAttributeQualifiers(Collection<AttributeDescriptorModel> attributeDescriptors) {
    return FluentIterable.from(attributeDescriptors).transform(new Function<AttributeDescriptorModel, String>() {
      @Override
      public String apply(AttributeDescriptorModel attribute) {
        return attribute.getQualifier();
      }
    }).toSet();
  }

  public static <K, V extends ItemModel> Map<K, PK> transformMapValuesToPks(Map<K, V> fromMap) {
    return Maps.transformValues(fromMap, new Function<V, PK>() {

      @Override
      public PK apply(V item) {
        return item.getPk();
      }
    });
  }

  public static <K, V extends AttributeDescriptorModel> Map<K, String> transformMapAttributeDescriptorValuesToString(
      Map<K, V> fromMap) {
    return Maps.transformValues(fromMap, new Function<V, String>() {

      @Override
      public String apply(V item) {
        return item.getQualifier();
      }
    });
  }

  public static <K, V extends ItemModel> Map<K, Set<PK>> transformMapCollectionValuesToPks(Map<K, Set<V>> fromMap) {
    return Maps.transformValues(fromMap, new Function<Collection<V>, Set<PK>>() {

      @Override
      public Set<PK> apply(Collection<V> items) {
        return extractPks(items);
      }

    });
  }

  public static <T> Map<String, Set<T>> transformMapComposedTypeKeyToCode(Map<ComposedTypeModel, Set<T>> source) {
    Map<String, Set<T>> result = new HashMap<>();
    for (Entry<ComposedTypeModel, Set<T>> entry : source.entrySet()) {
      result.put(entry.getKey().getCode(), entry.getValue());
    }
    return result;
  }

}
