package com.mirakl.hybris.facades.product.strategies.impl;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklPersistentOfferDataRetrievalStrategyUnitTest {

  private static final String PRODUCT_CODE = "product_code";

  @InjectMocks
  private DefaultMiraklPersistentOfferDataRetrievalStrategy defaultMiraklPersistentOfferDataRetrievalStrategy;

  @Mock
  private Converter<OfferModel, OfferData> offerConverter;
  @Mock
  private OfferService offerService;
  @Mock
  private OfferData firstOfferData, secondOfferData;
  @Mock
  private OfferModel firstOffer;
  @Mock
  private OfferModel secondOffer;

  @Test
  public void getOffersForProductWhenOffersFound() {
    when(offerService.getSortedOffersForProductCode(PRODUCT_CODE)).thenReturn(asList(firstOffer, secondOffer));
    when(offerConverter.convertAll(asList(firstOffer, secondOffer))).thenReturn(asList(firstOfferData, secondOfferData));

    List<OfferData> result = defaultMiraklPersistentOfferDataRetrievalStrategy.getOffersForProductCode(PRODUCT_CODE);

    assertThat(result).containsExactly(firstOfferData, secondOfferData);

    verify(offerService).getSortedOffersForProductCode(PRODUCT_CODE);
    verify(offerConverter).convertAll(asList(firstOffer, secondOffer));
  }

  @Test
  public void getOffersForProductWhenNoOffersFound() {
    when(offerService.getSortedOffersForProductCode(PRODUCT_CODE)).thenReturn(Collections.emptyList());
    when(offerConverter.convertAll(Collections.emptyList())).thenReturn(Collections.emptyList());

    List<OfferData> result = defaultMiraklPersistentOfferDataRetrievalStrategy.getOffersForProductCode(PRODUCT_CODE);

    assertThat(result).isEmpty();

    verify(offerService).getSortedOffersForProductCode(PRODUCT_CODE);
    verify(offerConverter).convertAll(Collections.emptyList());
  }
}
