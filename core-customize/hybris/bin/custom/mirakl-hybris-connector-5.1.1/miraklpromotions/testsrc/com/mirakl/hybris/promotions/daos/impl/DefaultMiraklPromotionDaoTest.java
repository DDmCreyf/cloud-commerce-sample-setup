package com.mirakl.hybris.promotions.daos.impl;

import static java.util.Collections.emptyList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklPromotionDaoTest {

  private static final String INTERNAL_ID = "internal-id";
  private static final String SHOP_ID = "shop-id";

  @InjectMocks
  private DefaultMiraklPromotionDao defaultMiraklPromotionDao;
  @Mock
  private FlexibleSearchService flexibleSearchService;
  @Mock
  private MiraklPromotionModel miraklPromotion;
  @Captor
  private ArgumentCaptor<FlexibleSearchQuery> flexibleSerachQueryCaptor;

  @Test
  public void shouldFindMiraklPromotion() throws Exception {
    when(flexibleSearchService.search(flexibleSerachQueryCaptor.capture()))
        .thenReturn(new SearchResultImpl<>(ImmutableList.<Object>of(miraklPromotion), 1, 0, 0));

    MiraklPromotionModel result = defaultMiraklPromotionDao.findMiraklPromotion(SHOP_ID, INTERNAL_ID);

    assertThat(result).isSameAs(miraklPromotion);
    FlexibleSearchQuery query = flexibleSerachQueryCaptor.getValue();
    assertThat(query.getQueryParameters().get(MiraklPromotionModel.INTERNALID)).isEqualTo(INTERNAL_ID);
    assertThat(query.getQueryParameters().get(MiraklPromotionModel.SHOPID)).isEqualTo(SHOP_ID);
  }

  @Test
  public void findMiraklPromotionShouldReturnNullIfNoPromotionFound() {
    when(flexibleSearchService.search(flexibleSerachQueryCaptor.capture()))
        .thenReturn(new SearchResultImpl<>(emptyList(), 1, 0, 0));

    MiraklPromotionModel result = defaultMiraklPromotionDao.findMiraklPromotion(SHOP_ID, INTERNAL_ID);

    assertThat(result).isNull();
    FlexibleSearchQuery query = flexibleSerachQueryCaptor.getValue();
    assertThat(query.getQueryParameters().get(MiraklPromotionModel.INTERNALID)).isEqualTo(INTERNAL_ID);
    assertThat(query.getQueryParameters().get(MiraklPromotionModel.SHOPID)).isEqualTo(SHOP_ID);
  }

  @Test(expected = AmbiguousIdentifierException.class)
  public void findMiraklPromotionThrowsAmbiguousIdentifierExceptionIfMultiplePromotionsFound() {
    when(flexibleSearchService.search(flexibleSerachQueryCaptor.capture()))
        .thenReturn(new SearchResultImpl<>(ImmutableList.<Object>of(miraklPromotion, miraklPromotion), 2, 0, 0));

    defaultMiraklPromotionDao.findMiraklPromotion(SHOP_ID, INTERNAL_ID);
    FlexibleSearchQuery query = flexibleSerachQueryCaptor.getValue();
    assertThat(query.getQueryParameters().get(MiraklPromotionModel.INTERNALID)).isEqualTo(INTERNAL_ID);
    assertThat(query.getQueryParameters().get(MiraklPromotionModel.SHOPID)).isEqualTo(SHOP_ID);
  }

}
