package com.mirakl.hybris.core.util;

import static com.google.common.collect.Sets.newHashSet;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DataModelUtilsTest {

  private static final String KEY3 = "key3";
  private static final String KEY2 = "key2";
  private static final String KEY1 = "key1";
  private static final PK ITEM1_PK = PK.fromLong(1L);
  private static final PK ITEM2_PK = PK.fromLong(2L);
  private static final PK ITEM3_PK = PK.fromLong(3L);
  @Mock
  private ItemModel item1, item2, item3;

  @Before
  public void setUp() throws Exception {
    when(item1.getPk()).thenReturn(ITEM1_PK);
    when(item2.getPk()).thenReturn(ITEM2_PK);
    when(item3.getPk()).thenReturn(ITEM3_PK);
  }

  @Test
  public void shouldExtractPks() {
    Set<PK> extractedPks = DataModelUtils.extractPks(Arrays.asList(item1, item2, item3));

    assertThat(extractedPks).containsOnly(ITEM1_PK, ITEM2_PK, ITEM3_PK);
  }

  @Test
  public void shouldTransformMapValuesToPks() {
    Map<Object, ItemModel> map = new HashMap<>();
    map.put(KEY1, item1);
    map.put(KEY2, item2);
    map.put(KEY3, item3);

    Map<Object, PK> transformedMap = DataModelUtils.transformMapValuesToPks(map);

    assertThat(transformedMap).hasSize(3);
    assertThat(transformedMap).includes(entry(KEY1, ITEM1_PK));
    assertThat(transformedMap).includes(entry(KEY2, ITEM2_PK));
    assertThat(transformedMap).includes(entry(KEY3, ITEM3_PK));
  }

  @Test
  public void shouldTransformMapCollectionValuesToPks() {
    Map<Object, Set<ItemModel>> map = new HashMap<>();
    map.put(KEY1, newHashSet(item1, item2));
    map.put(KEY2, newHashSet(item3));

    Map<Object, Set<PK>> transformedMap = DataModelUtils.transformMapCollectionValuesToPks(map);

    assertThat(transformedMap).hasSize(2);
    assertThat(transformedMap).includes(entry(KEY1, newHashSet(ITEM1_PK, ITEM2_PK)));
    assertThat(transformedMap).includes(entry(KEY2, newHashSet(ITEM3_PK)));
  }

}
