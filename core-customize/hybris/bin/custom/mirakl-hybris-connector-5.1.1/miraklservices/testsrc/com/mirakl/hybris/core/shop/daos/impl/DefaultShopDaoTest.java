package com.mirakl.hybris.core.shop.daos.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.util.flexiblesearch.QueryDecorator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultShopDaoTest {

  private static final String SHOP_ID = "shopId";
  public static final String PRODUCT_CODE = "product_code";

  @InjectMocks
  private DefaultShopDao shopDao;

  @Mock
  private FlexibleSearchService flexibleSearchService;
  @Mock
  private ShopModel firstShop, secondShop;

  @Mock
  private QueryDecorator queryDecorator;
  private List<QueryDecorator> queryDecorators;

  @Before
  public void setUp() {
    queryDecorators = asList(queryDecorator);
    shopDao.setQueryDecorators(queryDecorators);
  }

  @Test
  public void findsShopById() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(ImmutableList.<Object>of(firstShop), 1, 0, 0));

    ShopModel result = shopDao.findShopById(SHOP_ID);

    assertThat(result).isSameAs(firstShop);
  }

  @Test
  public void findShopByIdReturnsNullIfNoShopFound() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(new SearchResultImpl<>(emptyList(), 1, 0, 0));

    ShopModel result = shopDao.findShopById(SHOP_ID);

    assertThat(result).isNull();
  }

  @Test(expected = AmbiguousIdentifierException.class)
  public void findShopByIdThrowsAmbiguousIdentifierExceptionIfMultipleShopsFound() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(ImmutableList.<Object>of(firstShop, secondShop), 2, 0, 0));

    shopDao.findShopById(SHOP_ID);
  }

  @Test
  public void findShopsForProductCode() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class)))
        .thenReturn(new SearchResultImpl<>(ImmutableList.<Object>of(firstShop), 1, 0, 0));

    Collection<ShopModel> shops = shopDao.findShopsForProductCode(PRODUCT_CODE);

    assertThat(shops).containsOnly(firstShop);
  }

  @Test
  public void findShopsForProductCodeReturnsEmptyIdNoShopFound() {
    when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(new SearchResultImpl<>(emptyList(), 0, 0, 0));

    Collection<ShopModel> shops = shopDao.findShopsForProductCode(PRODUCT_CODE);

    assertThat(shops).isEmpty();
  }
}
