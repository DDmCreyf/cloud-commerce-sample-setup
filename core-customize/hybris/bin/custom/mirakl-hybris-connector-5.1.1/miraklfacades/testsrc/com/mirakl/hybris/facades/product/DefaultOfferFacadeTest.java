package com.mirakl.hybris.facades.product;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import com.mirakl.hybris.facades.product.impl.DefaultOfferFacade;
import com.mirakl.hybris.facades.product.strategies.impl.DefaultMiraklOfferDataRetrievalResolutionStrategy;
import com.mirakl.hybris.facades.product.strategies.impl.DefaultMiraklPersistentOfferDataRetrievalStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOfferFacadeTest {

  private static final String OFFER_ID = "offer-id";
  private static final String OFFER_CODE = "offer-code";
  private static final String PRODUCT_CODE = "product_code";
  private static final String UNKNOWN_OFFER_CODE = "Whatever..";

  @InjectMocks
  private DefaultOfferFacade defaultOfferFacade;

  @Mock
  private OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  @Mock
  private DefaultMiraklOfferDataRetrievalResolutionStrategy offerDataRetrievalResolutionStrategy;
  @Mock
  private DefaultMiraklPersistentOfferDataRetrievalStrategy offerDataRetrievalStrategy;
  @Mock
  private OfferService offerService;
  @Mock
  private OfferData firstOfferData, secondOfferData;
  @Mock
  private OfferModel offer;

  @Before
  public void setUp() {
    when(offerService.getOfferForId(OFFER_ID)).thenReturn(offer);
    when(offerCodeGenerationStrategy.isOfferCode(OFFER_CODE)).thenReturn(true);
    when(offerCodeGenerationStrategy.translateCodeToId(OFFER_CODE)).thenReturn(OFFER_ID);
    when(offerDataRetrievalResolutionStrategy.resolveOfferDataRetrievalStrategy()).thenReturn(offerDataRetrievalStrategy);
  }

  @Test
  public void getOffersForProductWhenOffersFound() {
    when(offerDataRetrievalStrategy.getOffersForProductCode(PRODUCT_CODE)).thenReturn(asList(firstOfferData, secondOfferData));
    List<OfferData> result = defaultOfferFacade.getOffersForProductCode(PRODUCT_CODE);

    assertThat(result).containsExactly(firstOfferData, secondOfferData);
    verify(offerDataRetrievalStrategy).getOffersForProductCode(PRODUCT_CODE);
  }

  @Test
  public void getOffersForProductWhenNoOffersFound() {
    when(offerDataRetrievalStrategy.getOffersForProductCode(PRODUCT_CODE)).thenReturn(Collections.emptyList());
    assertThat(defaultOfferFacade.getOffersForProductCode(PRODUCT_CODE)).isEmpty();
    verify(offerDataRetrievalStrategy).getOffersForProductCode(PRODUCT_CODE);
  }

  @Test
  public void getOfferForCode() {
    assertThat(defaultOfferFacade.getOfferForCode(OFFER_CODE)).isEqualTo(this.offer);
  }

  @Test(expected = UnknownIdentifierException.class)
  public void getOfferForUnknownCodeThrowsUnknownIdentifierException() {
    defaultOfferFacade.getOfferForCode(UNKNOWN_OFFER_CODE);
  }

  @Test
  public void getOffersForProductCodesWhenProductCodesEmpty() {
    Map<String, List<OfferData>> result = defaultOfferFacade.getOffersForProductCodes(Collections.emptyList());
    assertThat(result).isEmpty();
  }
  @Test
  public void getOffersForProductCodesWhenProductCodesNull() {
    Map<String, List<OfferData>> result = defaultOfferFacade.getOffersForProductCodes(null);
    assertThat(result).isEmpty();
  }

  @Test
  public void getOffersForProductCodesWhenStrategyResolved() {
    List<String> productCodes = Arrays.asList("code1", "code2");
    Map<String, List<OfferData>> mockMap = new HashMap<>();
    when(offerDataRetrievalResolutionStrategy.resolveOfferDataRetrievalStrategy()).thenReturn(offerDataRetrievalStrategy);
    when(offerDataRetrievalStrategy.getOffersForProductCodes(productCodes)).thenReturn(mockMap);
    Map<String, List<OfferData>> result = defaultOfferFacade.getOffersForProductCodes(productCodes);
    assertThat(result).isEqualTo(mockMap);
  }

  @Test
  public void getOffersForProductCodesWhenRetrievalStrategyIsNull() {
    List<String> productCodes = Arrays.asList("code1", "code2");
    when(offerDataRetrievalResolutionStrategy.resolveOfferDataRetrievalStrategy()).thenReturn(null);
    Map<String, List<OfferData>> result = defaultOfferFacade.getOffersForProductCodes(productCodes);
    assertThat(result).isEmpty();
  }

  @Test
  public void getOffersForProductCodesWhenStrategyGetOffersForProductCodesReturnsNull() {
    List<String> productCodes = Arrays.asList("code1", "code2");
    when(offerDataRetrievalResolutionStrategy.resolveOfferDataRetrievalStrategy()).thenReturn(offerDataRetrievalStrategy);
    when(offerDataRetrievalStrategy.getOffersForProductCodes(productCodes)).thenReturn(null);

    Map<String, List<OfferData>> result = defaultOfferFacade.getOffersForProductCodes(productCodes);

    assertThat(result).isEmpty();
  }
}
