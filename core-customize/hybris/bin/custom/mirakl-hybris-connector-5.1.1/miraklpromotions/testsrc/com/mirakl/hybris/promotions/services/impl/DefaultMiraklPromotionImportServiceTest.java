package com.mirakl.hybris.promotions.services.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotion;
import com.mirakl.client.mmp.front.domain.promotion.MiraklPromotions;
import com.mirakl.client.mmp.front.request.promotion.MiraklGetPromotionsRequest;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;
import com.mirakl.hybris.promotions.services.MiraklPromotionService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklPromotionImportServiceTest {

  private static final int MAX_RESULTS_BY_PAGE = 10;

  @Spy
  @InjectMocks
  private DefaultMiraklPromotionImportService promotionImportService;

  @Mock
  private ModelService modelService;
  @Mock
  private MiraklPromotionService miraklPromotionService;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Converter<MiraklPromotion, MiraklPromotionModel> miraklPromotionConverter;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklFrontApi;
  @Mock
  private MiraklPromotions miraklPromotions;
  @Mock
  private MiraklPromotion miraklPromotion;
  @Captor
  private ArgumentCaptor<MiraklGetPromotionsRequest> promotionsRequestCapture;

  private Map<MiraklPromotion, MiraklPromotionModel> promotionMocks;


  @Before
  public void setUp() throws Exception {
    doReturn(MAX_RESULTS_BY_PAGE).when(promotionImportService).getMaxResultsByPage();
    promotionMocks = getPromotionMocks(MAX_RESULTS_BY_PAGE);
    for (Entry<MiraklPromotion, MiraklPromotionModel> entry : promotionMocks.entrySet()) {
      when(miraklPromotionConverter.convert(entry.getKey())).thenReturn(entry.getValue());
    }
  }

  @Test
  public void shouldImportAllPromotionsWithNoPagination() {
    when(miraklPromotions.getPromotions()).thenReturn(new ArrayList<>(promotionMocks.keySet()));
    when(miraklFrontApi.getPromotions(promotionsRequestCapture.capture())).thenReturn(miraklPromotions);

    Collection<MiraklPromotionModel> importedPromotions = promotionImportService.importAllPromotions();

    MiraklGetPromotionsRequest request = promotionsRequestCapture.getValue();
    assertThat(request.getMax(), equalTo(MAX_RESULTS_BY_PAGE));
    assertThat(importedPromotions, hasSize(miraklPromotions.getPromotions().size()));
    verify(miraklFrontApi).getPromotions(request);
  }

  @Test
  public void shouldHandlePaginationWhenTotalCountIsMultipleOfMaxPerPage() {
    when(miraklPromotions.getPromotions()).thenReturn(new ArrayList<>(promotionMocks.keySet()));
    when(miraklPromotions.getTotalCount()).thenReturn(MAX_RESULTS_BY_PAGE * 3L);
    when(miraklFrontApi.getPromotions(promotionsRequestCapture.capture())).thenReturn(miraklPromotions);

    promotionImportService.importAllPromotions();

    promotionsRequestCapture.getValue();
    verify(miraklFrontApi, times(3)).getPromotions(Mockito.any(MiraklGetPromotionsRequest.class));
  }

  @Test
  public void shouldHandlePaginationWhenTotalCountIsNotMultipleOfMaxPerPage() {
    when(miraklPromotions.getPromotions()).thenReturn(new ArrayList<>(promotionMocks.keySet()));
    when(miraklPromotions.getTotalCount()).thenReturn(MAX_RESULTS_BY_PAGE * 3L + 1);
    when(miraklFrontApi.getPromotions(promotionsRequestCapture.capture())).thenReturn(miraklPromotions);

    promotionImportService.importAllPromotions();

    promotionsRequestCapture.getValue();
    verify(miraklFrontApi, times(4)).getPromotions(Mockito.any(MiraklGetPromotionsRequest.class));
  }

  private Map<MiraklPromotion, MiraklPromotionModel> getPromotionMocks(int size) {
    Map<MiraklPromotion, MiraklPromotionModel> mocks = new HashMap<>();
    for (int i = 0; i < size; i++) {
      mocks.put(mock(MiraklPromotion.class), mock(MiraklPromotionModel.class));
    }
    return mocks;
  }

}
