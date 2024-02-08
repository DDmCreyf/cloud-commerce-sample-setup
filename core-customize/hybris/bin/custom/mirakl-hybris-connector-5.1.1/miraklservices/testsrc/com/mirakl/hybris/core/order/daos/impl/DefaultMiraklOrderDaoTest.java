package com.mirakl.hybris.core.order.daos.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklOrderDaoTest {

  @InjectMocks
  private DefaultMiraklOrderDao orderDao;

  @Mock
  private FlexibleSearchService flexibleSearchService;

  @Mock
  private CartEntryModel cartEntry;

  @Mock
  private CartModel cartModel;

  @Mock
  private ProductModel productModel;


  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void shouldFindEntriesByProductWothNoOffers() {
    when(flexibleSearchService.search(anyString(), anyMapOf(String.class, Object.class)))
        .thenReturn(new SearchResultImpl<>(Collections.<Object>singletonList(cartEntry), 1, 0, 0));

    List<AbstractOrderEntryModel> cartEntries = orderDao.findEntriesByProduct(cartModel, productModel);

    assertThat(cartEntries).containsOnly(cartEntry);
  }

}
