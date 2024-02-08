package com.mirakl.hybris.facades.search.solrfacetsearch.provider.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.services.ShopService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ShopFacetDisplayNameProviderTest {

  private static final String OPERATOR_CODE = "operator-code";
  private static final String OPERATOR_NAME = "operator-name";
  private static final String SHOP_ID = "shop-id";
  private static final String SHOP_NAME = "shop-name";

  @InjectMocks
  private ShopFacetDisplayNameProvider displayNameProvider;

  @Mock
  private ShopService shopService;

  @Mock
  private SearchQuery searchQuery;

  @Mock
  private IndexedProperty indexedProperty;

  @Mock
  private FacetSearchConfig facetSearchConfig;

  @Mock
  private IndexConfig indexConfig;

  @Mock
  private BaseSiteModel baseSite;

  @Mock
  private ShopModel shop;

  @Before
  public void setUp() throws Exception {
    when(shopService.getShopForId(SHOP_ID)).thenReturn(shop);
    when(shop.getName()).thenReturn(SHOP_NAME);
    when(searchQuery.getFacetSearchConfig()).thenReturn(facetSearchConfig);
    when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
    when(indexConfig.getBaseSite()).thenReturn(baseSite);
    when(baseSite.getOperatorCode()).thenReturn(OPERATOR_CODE);
    when(baseSite.getOperatorName()).thenReturn(OPERATOR_NAME);
  }

  @Test
  public void shouldDisplayShopNameIfShopFound() {
    String displayName = displayNameProvider.getDisplayName(searchQuery, indexedProperty, SHOP_ID);

    assertThat(displayName).isEqualTo(SHOP_NAME);
  }

  @Test
  public void shouldDisplayIdIfShopNotFound() {
    when(shopService.getShopForId(SHOP_ID)).thenReturn(null);

    String displayName = displayNameProvider.getDisplayName(searchQuery, indexedProperty, SHOP_ID);

    assertThat(displayName).isEqualTo(SHOP_ID);
  }

  @Test
  public void shouldDisplayOperatorNameIfOperatorCode() {
    String displayName = displayNameProvider.getDisplayName(searchQuery, indexedProperty, OPERATOR_CODE);

    assertThat(displayName).isEqualTo(OPERATOR_NAME);
  }

}
