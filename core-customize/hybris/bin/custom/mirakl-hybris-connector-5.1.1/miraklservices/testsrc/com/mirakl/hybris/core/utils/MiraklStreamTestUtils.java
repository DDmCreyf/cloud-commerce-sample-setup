package com.mirakl.hybris.core.utils;

import java.io.IOException;
import java.util.Iterator;

import com.google.common.collect.Lists;
import com.mirakl.client.core.internal.MiraklStream;

public class MiraklStreamTestUtils {

  private MiraklStreamTestUtils() {};

  public static <T> MiraklStream<T> getMiraklStream(@SuppressWarnings("unchecked") final T... items) {
    return new MiraklStream<T>() {

      @Override
      public Iterator<T> iterator() {
        return Lists.<T>newArrayList(items).iterator();
      }

      @Override
      public void close() throws IOException {}
    };

  }
}
