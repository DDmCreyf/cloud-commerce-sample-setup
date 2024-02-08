package com.mirakl.hybris.core.ordersplitting.daos.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collections;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConsignmentEntryDaoTest {

  private static final String CONSIGNMENT_ENTRY_CODE = "consignment entry code";

  @InjectMocks
  DefaultConsignmentEntryDao testObj;

  @Mock
  FlexibleSearchService flexibleSearchService;

  @Mock
  ConsignmentEntryModel consignmentEntry;

  @Test
  public void findConsignmentByCode() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
            .thenReturn(new SearchResultImpl<>(ImmutableList.<Object>of(consignmentEntry), 1, 0, 0));

    ConsignmentEntryModel consignmentEntry = testObj.findConsignmentEntryByMiraklLineId(CONSIGNMENT_ENTRY_CODE);

    assertThat(consignmentEntry).isEqualTo(consignmentEntry);
  }

  @Test
  public void findConsignmentByCodeWhenNotFound() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
            .thenReturn(new SearchResultImpl<>(Collections.emptyList(), 1, 0, 0));

    ConsignmentEntryModel consignmentEntry = testObj.findConsignmentEntryByMiraklLineId(CONSIGNMENT_ENTRY_CODE);

    assertThat(consignmentEntry).isNull();
  }

  @Test(expected = AmbiguousIdentifierException.class)
  public void findConsignmentByCodeWhenMoreThanOneEntryFound() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
            .thenReturn(new SearchResultImpl<>(ImmutableList.<Object>of(consignmentEntry, consignmentEntry), 1, 0, 0));

    ConsignmentEntryModel consignmentEntry = testObj.findConsignmentEntryByMiraklLineId(CONSIGNMENT_ENTRY_CODE);
  }

}
