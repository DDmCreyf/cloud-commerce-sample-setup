package com.mirakl.hybris.core.product.services.impl;

import static java.util.Collections.emptyList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.offer.MiraklProductInformationWithReferences;
import com.mirakl.client.mmp.domain.product.MiraklOfferOnProduct;
import com.mirakl.client.mmp.domain.product.MiraklProductWithOffers;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.product.MiraklGetOffersOnProductsRequest;
import com.mirakl.hybris.core.product.factories.MiraklGetOffersOnProductsRequestFactory;
import com.mirakl.hybris.core.product.strategies.MiraklRealtimeOffersFilteringStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklRealtimeOfferServiceTest {
  private static final String PRODUCT_CODE_1 = "PRODUCT-CODE-1";
  private static final String PRODUCT_CODE_2 = "PRODUCT-CODE-2";
  private static final String UNKNOWN_PRODUCT = "UNKNOWN_PRODUCT";

  @InjectMocks
  private DefaultMiraklRealtimeOfferService defaultMiraklRealtimeOfferService;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklFrontApi;
  @Mock
  private MiraklGetOffersOnProductsRequestFactory requestFactory;
  @Mock
  private MiraklGetOffersOnProductsRequest mockedRequest;
  @Mock
  private MiraklRealtimeOffersFilteringStrategy realtimeOffersFilteringStrategy;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;

  @Before
  public void setUp() throws Exception {
    when(configurationService.getConfiguration()).thenReturn(configuration);
    when(configuration.getInt(anyString(), anyInt())).thenReturn(10);
    when(requestFactory.buildProductsOfferSearchRequest(anyList())).thenReturn(mockedRequest);
    when(realtimeOffersFilteringStrategy.getFilteredProductCodes(anyList())).thenAnswer((Answer<List<String>>) invocation -> (List<String>) invocation.getArguments()[0]);

  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldNotCallApiIfInputListIsNullYetShouldReturnNonNull() {
    Map<String, List<MiraklOfferOnProduct>> offersOnProducts = defaultMiraklRealtimeOfferService.getOffersForProductCodes(null);
    assertThat(offersOnProducts).isEmpty();
    verify(realtimeOffersFilteringStrategy, never()).getFilteredProductCodes(emptyList());
    verify(miraklFrontApi, never()).getOffersOnProducts((MiraklGetOffersOnProductsRequest) any());
    verify(realtimeOffersFilteringStrategy, never()).getFilteredOffers(any(MiraklProductWithOffers.class));
  }

  @Test
  public void shouldNotCallApiIfInputListIEmptyYetShouldReturnNonNull() {
    Map<String, List<MiraklOfferOnProduct>> offersOnProducts = defaultMiraklRealtimeOfferService.getOffersForProductCodes(emptyList());
    assertThat(offersOnProducts).isEmpty();
    verify(realtimeOffersFilteringStrategy).getFilteredProductCodes(emptyList());
    verify(miraklFrontApi, never()).getOffersOnProducts((MiraklGetOffersOnProductsRequest) any());
    verify(realtimeOffersFilteringStrategy, never()).getFilteredOffers(any(MiraklProductWithOffers.class));
  }

  @Test
  public void shouldReturnEmptyOfferListIfErrorOccurs() {
    Mockito.doThrow(MiraklApiException.ioException(new IOException("any error"))).when(miraklFrontApi)
        .getOffersOnProducts((MiraklGetOffersOnProductsRequest) any());
    Map<String, List<MiraklOfferOnProduct>> offersOnProduct =
        defaultMiraklRealtimeOfferService.getOffersForProductCodes(Collections.singletonList(PRODUCT_CODE_1));
    verify(realtimeOffersFilteringStrategy).getFilteredProductCodes(Collections.singletonList(PRODUCT_CODE_1));

    verify(realtimeOffersFilteringStrategy, never()).getFilteredOffers(any(MiraklProductWithOffers.class));
    assertThat(offersOnProduct).isNotNull();
    assertThat(offersOnProduct).isEmpty();
  }

  @Test
  public void shouldReturnResultForProductsWithNoOffersInDatabase() {
    final List<MiraklProductWithOffers> productWithOffers =
        Collections.singletonList(new ProductWithOffersBuilder().forProduct(PRODUCT_CODE_1).withOffers(3, 3).build());

    final MiraklGetOffersOnProductsRequest request1 = Mockito.mock(MiraklGetOffersOnProductsRequest.class);
    when(requestFactory.buildProductsOfferSearchRequest(anyList())).thenReturn(request1);
    //unknown product is filtered out
    when(realtimeOffersFilteringStrategy.getFilteredProductCodes(anyList())).thenReturn(Collections.singletonList(PRODUCT_CODE_1));
    when(miraklFrontApi.getOffersOnProducts((MiraklGetOffersOnProductsRequest) any())).thenReturn(productWithOffers);
    when(realtimeOffersFilteringStrategy.getFilteredOffers(any(MiraklProductWithOffers.class)))
        .thenAnswer(invocation -> ((MiraklProductWithOffers) invocation.getArguments()[0]).getOffers());

    Map<String, List<MiraklOfferOnProduct>> offersOnProducts =
        defaultMiraklRealtimeOfferService.getOffersForProductCodes(Arrays.asList(PRODUCT_CODE_1, UNKNOWN_PRODUCT));

    verify(realtimeOffersFilteringStrategy).getFilteredProductCodes(Arrays.asList(PRODUCT_CODE_1, UNKNOWN_PRODUCT));
    verify(miraklFrontApi).getOffersOnProducts((MiraklGetOffersOnProductsRequest) any());
    verify(realtimeOffersFilteringStrategy).getFilteredOffers(any(MiraklProductWithOffers.class));

    assertThat(offersOnProducts).hasSize(2);
    assertThat(offersOnProducts.keySet()).containsOnly(PRODUCT_CODE_1,UNKNOWN_PRODUCT);
    assertThat(offersOnProducts.get(PRODUCT_CODE_1)).hasSize(3);
    assertThat(offersOnProducts.get(UNKNOWN_PRODUCT)).isEmpty();
  }

  @Test
  public void shouldReturnResultForProductsNotFoundInMirakl() {
    final List<MiraklProductWithOffers> productWithOffers =
        Collections.singletonList(new ProductWithOffersBuilder().forProduct(PRODUCT_CODE_1).withOffers(3, 3).build());

    final MiraklGetOffersOnProductsRequest request1 = Mockito.mock(MiraklGetOffersOnProductsRequest.class);
    when(requestFactory.buildProductsOfferSearchRequest(anyList())).thenReturn(request1);
    when(miraklFrontApi.getOffersOnProducts((MiraklGetOffersOnProductsRequest) any())).thenReturn(productWithOffers);
    when(realtimeOffersFilteringStrategy.getFilteredOffers(any(MiraklProductWithOffers.class)))
        .thenAnswer(invocation -> ((MiraklProductWithOffers) invocation.getArguments()[0]).getOffers());

    Map<String, List<MiraklOfferOnProduct>> offersOnProducts =
        defaultMiraklRealtimeOfferService.getOffersForProductCodes(Arrays.asList(PRODUCT_CODE_1, UNKNOWN_PRODUCT));

    verify(realtimeOffersFilteringStrategy).getFilteredProductCodes(Arrays.asList(PRODUCT_CODE_1, UNKNOWN_PRODUCT));
    verify(miraklFrontApi).getOffersOnProducts((MiraklGetOffersOnProductsRequest) any());
    verify(realtimeOffersFilteringStrategy).getFilteredOffers(any(MiraklProductWithOffers.class));

    assertThat(offersOnProducts).hasSize(2);
    assertThat(offersOnProducts.keySet()).containsOnly(PRODUCT_CODE_1,UNKNOWN_PRODUCT);
    assertThat(offersOnProducts.get(PRODUCT_CODE_1)).hasSize(3);
    assertThat(offersOnProducts.get(UNKNOWN_PRODUCT)).isEmpty();
  }


  @Test
  public void shouldPaginateOnePageFor1Product() {
    final List<MiraklProductWithOffers> productWithOffers =
        Collections.singletonList(new ProductWithOffersBuilder().forProduct(PRODUCT_CODE_1).withOffers(3, 3).build());
    final MiraklGetOffersOnProductsRequest request1 = Mockito.mock(MiraklGetOffersOnProductsRequest.class);
    when(requestFactory.buildProductsOfferSearchRequest(anyList())).thenReturn(request1);
    when(miraklFrontApi.getOffersOnProducts((MiraklGetOffersOnProductsRequest) any())).thenReturn(productWithOffers);
    when(realtimeOffersFilteringStrategy.getFilteredOffers(any(MiraklProductWithOffers.class)))
        .thenAnswer(invocation -> ((MiraklProductWithOffers) invocation.getArguments()[0]).getOffers());

    Map<String, List<MiraklOfferOnProduct>> offersOnProducts =
        defaultMiraklRealtimeOfferService.getOffersForProductCodes(Collections.singletonList(PRODUCT_CODE_1));

    verify(realtimeOffersFilteringStrategy).getFilteredProductCodes(Collections.singletonList(PRODUCT_CODE_1));
    verify(miraklFrontApi).getOffersOnProducts((MiraklGetOffersOnProductsRequest) any());
    verify(realtimeOffersFilteringStrategy).getFilteredOffers(any(MiraklProductWithOffers.class));
    assertThat(offersOnProducts).hasSize(1);
    assertThat(offersOnProducts.get(PRODUCT_CODE_1)).hasSize(3);
  }

  @Test
  public void shouldPaginate2PagesFor1Product() {
    final List<MiraklProductWithOffers> firstCallToProductWithOffers =
        Collections.singletonList(new ProductWithOffersBuilder().forProduct(PRODUCT_CODE_1).withOffers(10, 11).build());
    final List<MiraklProductWithOffers> secondCallToProductWithOffers =
        Collections.singletonList(new ProductWithOffersBuilder().forProduct(PRODUCT_CODE_1).withOffers(1, 11).build());
    when(miraklFrontApi.getOffersOnProducts((MiraklGetOffersOnProductsRequest) any())).thenReturn(firstCallToProductWithOffers,
        secondCallToProductWithOffers);
    when(realtimeOffersFilteringStrategy.getFilteredOffers(any(MiraklProductWithOffers.class)))
        .thenAnswer(invocation -> ((MiraklProductWithOffers) invocation.getArguments()[0]).getOffers());

    Map<String, List<MiraklOfferOnProduct>> offersOnProducts =
        defaultMiraklRealtimeOfferService.getOffersForProductCodes(Collections.singletonList(PRODUCT_CODE_1));

    verify(realtimeOffersFilteringStrategy).getFilteredProductCodes(Collections.singletonList(PRODUCT_CODE_1));
    verify(miraklFrontApi, Mockito.times(2)).getOffersOnProducts((MiraklGetOffersOnProductsRequest) any());
    verify(realtimeOffersFilteringStrategy).getFilteredOffers(any(MiraklProductWithOffers.class));
    assertThat(offersOnProducts).hasSize(1);
    assertThat(offersOnProducts.get(PRODUCT_CODE_1)).hasSize(11);
  }


  @Test
  public void shouldPaginate3PagesFor2ProductIfNeededForOneOfTheProducts() {

    final List<MiraklProductWithOffers> firstCallToProductWithOffersOnP1AndP2 =
        Arrays.asList(new ProductWithOffersBuilder().forProduct(PRODUCT_CODE_1).withOffers(10, 24).build(),
            new ProductWithOffersBuilder().forProduct(PRODUCT_CODE_2).withOffers(3, 3).build());
    final List<MiraklProductWithOffers> secondCallToProductWithOffersOnP1AndP2 =
        Arrays.asList(new ProductWithOffersBuilder().forProduct(PRODUCT_CODE_1).withOffers(10, 24).build(),
            new ProductWithOffersBuilder().forProduct(PRODUCT_CODE_2).withOffers(0, 3).build());
    final List<MiraklProductWithOffers> thirdCallToProductWithOffersOnP1AndP2 =
        Arrays.asList(new ProductWithOffersBuilder().forProduct(PRODUCT_CODE_1).withOffers(4, 24).build(),
            new ProductWithOffersBuilder().forProduct(PRODUCT_CODE_2).withOffers(0, 3).build());

    when(miraklFrontApi.getOffersOnProducts((MiraklGetOffersOnProductsRequest) any()))
        .thenReturn(firstCallToProductWithOffersOnP1AndP2, secondCallToProductWithOffersOnP1AndP2, thirdCallToProductWithOffersOnP1AndP2);
    when(realtimeOffersFilteringStrategy.getFilteredOffers(any(MiraklProductWithOffers.class)))
        .thenAnswer(invocation -> ((MiraklProductWithOffers) invocation.getArguments()[0]).getOffers());

    Map<String, List<MiraklOfferOnProduct>> offersOnProducts =
        defaultMiraklRealtimeOfferService.getOffersForProductCodes(Arrays.asList(PRODUCT_CODE_1, PRODUCT_CODE_2));

    verify(realtimeOffersFilteringStrategy).getFilteredProductCodes(Arrays.asList(PRODUCT_CODE_1, PRODUCT_CODE_2));
    verify(realtimeOffersFilteringStrategy, Mockito.times(2)).getFilteredOffers(any(MiraklProductWithOffers.class));

    assertThat(offersOnProducts).hasSize(2);
    assertThat(offersOnProducts.get(PRODUCT_CODE_1)).hasSize(24);
    assertThat(offersOnProducts.get(PRODUCT_CODE_2)).hasSize(3);

  }



  private static class ProductWithOffersBuilder {

    private final MiraklProductWithOffers productWithOffers = new MiraklProductWithOffers();

    public ProductWithOffersBuilder forProduct(final String productId) {
      MiraklProductInformationWithReferences ref = new MiraklProductInformationWithReferences();
      ref.setSku(productId);
      productWithOffers.setProduct(ref);
      return this;
    }

    public ProductWithOffersBuilder withOffers(final int nbPaginatedOffers, final int nbTotalOffers) {
      productWithOffers.setOffers(IntStream.range(0, nbPaginatedOffers).mapToObj(index -> {
        final MiraklOfferOnProduct offer = new MiraklOfferOnProduct();
        offer.setId(String.valueOf(index));
        return offer;
      }).collect(Collectors.toList()));
      productWithOffers.setTotalCount(nbTotalOffers);
      return this;
    }

    public MiraklProductWithOffers build() {
      return productWithOffers;
    }
  }
}

