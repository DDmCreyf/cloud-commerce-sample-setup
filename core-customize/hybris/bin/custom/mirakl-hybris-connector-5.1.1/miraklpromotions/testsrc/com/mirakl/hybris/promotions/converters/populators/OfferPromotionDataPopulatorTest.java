package com.mirakl.hybris.promotions.converters.populators;

import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklPromotionData;
import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.promotions.strategies.MiraklPromotionsActivationStrategy;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;
import com.mirakl.hybris.promotions.services.MiraklPromotionService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OfferPromotionDataPopulatorTest {

  @InjectMocks
  private OfferPromotionDataPopulator testObj;

  @Mock
  private Converter<MiraklPromotionModel, MiraklPromotionData> miraklPromotionDataConverter;
  @Mock
  private MiraklPromotionService miraklPromotionService;
  @Mock
  private MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy;
  @Mock
  private MiraklPromotionModel miraklPromotion;
  @Mock
  private OfferModel offer;
  @Mock
  private MiraklPromotionData miraklPromotionData;

  @Before
  public void setUp() throws Exception {
    when(miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled()).thenReturn(true);
    List<MiraklPromotionModel> miraklPromotions = singletonList(miraklPromotion);
    when(miraklPromotionService.getPromotionsForOffer(offer, true)).thenReturn(miraklPromotions);
    when(miraklPromotionDataConverter.convertAll(miraklPromotions)).thenReturn(singletonList(miraklPromotionData));
  }

  @Test
  public void populate() throws Exception {
    OfferData output = new OfferData();

    testObj.populate(offer, output);

    assertThat(output.getPromotions()).containsOnly(miraklPromotionData);
  }

  @Test
  public void shouldNotPopulateWhenMiraklPromotionIsDisabled() throws Exception {
    when(miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled()).thenReturn(false);
    OfferData output = new OfferData();

    testObj.populate(offer, output);

    assertThat(output.getPromotions()).isEmpty();
  }

}
