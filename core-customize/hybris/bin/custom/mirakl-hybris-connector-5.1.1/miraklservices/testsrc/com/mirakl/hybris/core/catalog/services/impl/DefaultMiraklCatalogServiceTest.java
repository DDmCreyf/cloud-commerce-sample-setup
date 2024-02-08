package com.mirakl.hybris.core.catalog.services.impl;


import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mci.domain.attribute.MiraklAttribute;
import com.mirakl.client.mci.domain.hierarchy.MiraklHierarchy;
import com.mirakl.client.mci.domain.value.list.MiraklValueList;
import com.mirakl.client.mci.domain.value.list.MiraklValueListItem;
import com.mirakl.client.mci.domain.value.list.MiraklValueLists;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mci.front.request.attribute.MiraklGetAttributesRequest;
import com.mirakl.client.mci.front.request.hierarchy.MiraklGetHierarchiesRequest;
import com.mirakl.client.mci.front.request.value.list.MiraklGetValueListsItemsRequest;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklCatalogServiceTest {

  private static final String HIERARCHY_1_CODE = "golf";
  private static final String HIERARCHY_2_CODE = "shoes";
  private static final String HIERARCHY_3_CODE = "weights";
  private static final String ATTRIBUTE_1_CODE = "size";
  private static final String ATTRIBUTE_2_CODE = "weight";
  private static final String VALUE_LIST_1_CODE = "small";
  private static final String VALUE_1_CODE = "value 1 code";
  private static final String VALUE_2_CODE = "value 2 code";

  private MiraklHierarchy hierarchy1, hierarchy2, hierarchy3;
  private MiraklAttribute attribute1, attribute2;
  private MiraklValueLists miraklValueLists;
  private MiraklValueList valueList;
  private MiraklValueListItem value1, value2;

  @Mock
  private MiraklCatalogIntegrationFrontApi mciApi;

  @InjectMocks
  private DefaultMiraklCatalogService testObj;

  @Before
  public void setUp() {
    initTestHierarchies();
    initTestAttributes();
    initTestValueList();
    when(mciApi.getHierarchies(any(MiraklGetHierarchiesRequest.class))).thenReturn(asList(hierarchy1, hierarchy2, hierarchy3));
    when(mciApi.getAttributes(any(MiraklGetAttributesRequest.class))).thenReturn(asList(attribute1, attribute2));
    when(mciApi.getValueLists(any(MiraklGetValueListsItemsRequest.class))).thenReturn(miraklValueLists);
  }

  @Test
  public void getMiraklCategoryCodes() throws Exception {
    Set<String> output = testObj.getMiraklCategoryCodes();

    verify(mciApi).getHierarchies(any(MiraklGetHierarchiesRequest.class));
    assertThat(output).contains(HIERARCHY_1_CODE, HIERARCHY_2_CODE, HIERARCHY_3_CODE);
  }

  @Test
  public void getMiraklAttributeCodes() throws Exception {
    Set<Pair<String, String>> output = testObj.getMiraklAttributeCodes();

    verify(mciApi).getAttributes(any(MiraklGetAttributesRequest.class));
    assertThat(output).hasSize(2);
  }

  @Test
  public void getMiraklValueListItemCodes() throws Exception {
    Set<Pair<String, String>> output = testObj.getMiraklValueCodes();

    verify(mciApi).getValueLists(any(MiraklGetValueListsItemsRequest.class));
    assertThat(output).hasSize(2);
  }

  private void initTestAttributes() {
    attribute1 = new MiraklAttribute();
    attribute1.setCode(ATTRIBUTE_1_CODE);
    attribute1.setHierarchyCode(HIERARCHY_2_CODE);
    attribute2 = new MiraklAttribute();
    attribute2.setCode(ATTRIBUTE_2_CODE);
    attribute2.setHierarchyCode(HIERARCHY_3_CODE);
  }

  private void initTestHierarchies() {
    hierarchy1 = new MiraklHierarchy();
    hierarchy1.setCode(HIERARCHY_1_CODE);
    hierarchy2 = new MiraklHierarchy();
    hierarchy2.setCode(HIERARCHY_2_CODE);
    hierarchy3 = new MiraklHierarchy();
    hierarchy3.setCode(HIERARCHY_3_CODE);
  }

  private void initTestValueList() {
    miraklValueLists = new MiraklValueLists();
    value1 = new MiraklValueListItem();
    value1.setCode(VALUE_1_CODE);
    value2 = new MiraklValueListItem();
    value2.setCode(VALUE_2_CODE);
    valueList = new MiraklValueList();
    valueList.setCode(VALUE_LIST_1_CODE);
    valueList.setItems(asList(value1, value2));
    miraklValueLists.setValueLists(singletonList(valueList));
  }

}
