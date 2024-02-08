package com.mirakl.hybris.facades.search.solrfacetsearch.populators.impl;

import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.mirakl.hybris.facades.search.solrfacetsearch.populators.impl.MiraklOfferOverviewDataPopulator.*;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklOfferOverviewDataPopulatorTest {

  private static final String OFFER_STATE_CODE = "offer-state";
  private static final OfferState OFFER_STATE = OfferState.valueOf(OFFER_STATE_CODE);
  private static final String OFFER_ID = "id";
  private static final String OFFER_CODE = "offer_code";
  private static final String SHOP_ID = "shop-id";
  private static final String SHOP_NAME = "shop-name";
  private static final double SHOP_GRADE = 3;

  @InjectMocks
  private MiraklOfferOverviewDataPopulator populator;

  @Mock
  private OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  private final OfferModel offer = new OfferModel();
  private final ShopModel shop = new ShopModel();

  @Before
  public void setUp() throws Exception {
    when(offerCodeGenerationStrategy.generateCode(OFFER_ID)).thenReturn(OFFER_CODE);

    shop.setId(SHOP_ID);
    shop.setName(SHOP_NAME);
    shop.setGrade(SHOP_GRADE);
    offer.setShop(shop);

    offer.setId(OFFER_ID);
    offer.setShop(shop);
    offer.setState(OFFER_STATE);
  }

  @Test
  public void shouldPopulateOfferSummary() {
    OfferOverviewData result = new OfferOverviewData();
    populator.populate(offer, result);

    assertThat(result.getCode()).isEqualTo(OFFER_CODE);
    assertThat(result.getShopId()).isEqualTo(SHOP_ID);
    assertThat(result.getShopName()).isEqualTo(SHOP_NAME);
    assertThat(result.getShopGrade()).isEqualTo(SHOP_GRADE);
    assertThat(result.getStateCode()).isEqualTo(OFFER_STATE_CODE);

  }

  @Test
  public void shouldPopulateMinPurchasableQty() {
    offer.setMinOrderQuantity(12);

    OfferOverviewData result = new OfferOverviewData();
    populator.populate(offer, result);

    assertThat(result.getMinPurchasableQty()).isEqualTo(12);
  }

  @Test
  public void shouldPopulatePackageQuantity() {
    offer.setPackageQuantity(5);

    OfferOverviewData result = new OfferOverviewData();
    populator.populate(offer, result);

    assertThat(result.getPackageQty()).isEqualTo(5);
  }


  @Test
  public void shouldPopulateMaxPurchasableQty() {
    offer.setMaxOrderQuantity(5);

    OfferOverviewData result = new OfferOverviewData();
    populator.populate(offer, result);

    assertThat(result.getMaxPurchasableQty()).isEqualTo(5);
  }

}
