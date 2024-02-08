package com.mirakl.hybris.core.order.daos.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklAbstractOrderEntryDaoTest {

  private static final String OFFER_ID = "offer-id";

  @InjectMocks
  private DefaultMiraklCartEntryDao cartEntryDao;

  @Mock
  private FlexibleSearchService flexibleSearchService;

  @Mock
  private CartEntryModel cartEntry;

  @Mock
  private CartEntryModel otherCartEntry;

  @Mock
  private OfferModel offer;

  @Mock
  private CartModel cart;

  @Before
  public void setUp() {
    when(offer.getId()).thenReturn(OFFER_ID);
  }

  @Test
  public void shouldFindEntryByOffer() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(Collections.<Object>singletonList(cartEntry), 1, 0, 0));

    CartEntryModel result = cartEntryDao.findEntryByOffer(cart, offer);

    assertThat(result).isEqualTo(cartEntry);
  }

  @Test
  public void shouldReturnNullForNotFindingEntryByOffer() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(Collections.emptyList(), 1, 0, 0));

    CartEntryModel result = cartEntryDao.findEntryByOffer(cart, offer);

    assertThat(result).isEqualTo(null);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldFindByOfferThrowExceptionForMoreThanASingleResult() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(Arrays.<Object>asList(cartEntry, otherCartEntry), 1, 0, 0));

    cartEntryDao.findEntryByOffer(cart, offer);
  }

}
