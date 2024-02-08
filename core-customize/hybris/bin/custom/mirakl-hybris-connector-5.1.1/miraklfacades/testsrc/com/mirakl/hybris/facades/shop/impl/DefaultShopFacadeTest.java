package com.mirakl.hybris.facades.shop.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.evaluation.MiraklEvaluation;
import com.mirakl.client.mmp.domain.evaluation.MiraklEvaluations;
import com.mirakl.hybris.beans.EvaluationData;
import com.mirakl.hybris.beans.EvaluationPageData;
import com.mirakl.hybris.beans.ShopData;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.services.ShopService;
import com.mirakl.hybris.facades.shop.ShopFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;



@UnitTest
@RunWith(value = MockitoJUnitRunner.class)
public class DefaultShopFacadeTest {

  public static final String SHOP_ID = "shop_id";
  public static final long EVALUATION_TOTAL_COUNT = 50;

  @Mock
  private Converter<ShopModel, ShopData> shopDataConverter;

  @Mock
  private Converter<MiraklEvaluation, EvaluationData> evaluationDataConverter;

  @Mock
  private ShopService shopService;

  @Mock
  private ShopModel shopModel;

  @Mock
  private MiraklEvaluations miraklEvaluations;

  @Mock
  private ShopData shopData;

  @Mock
  private PageableData pageableData;

  @Mock
  private EvaluationData evaluationData;

  @InjectMocks
  private ShopFacade testObj = new DefaultShopFacade();

  @Before
  public void setUp() {
    when(shopService.getShopForId(SHOP_ID)).thenReturn(shopModel);
    when(shopDataConverter.convert(shopModel)).thenReturn(shopData);
    when(shopService.getEvaluations(SHOP_ID, pageableData)).thenReturn(miraklEvaluations);
    when(miraklEvaluations.getTotalCount()).thenReturn(EVALUATION_TOTAL_COUNT);
    when(miraklEvaluations.getEvaluations()).thenReturn(Collections.singletonList(new MiraklEvaluation()));
    when(evaluationDataConverter.convertAll(anyCollectionOf(MiraklEvaluation.class)))
    .thenReturn(Collections.singletonList(evaluationData));
    when(pageableData.getPageSize()).thenReturn(10);
  }

  @Test
  public void getShopWhenIdExists() {
    assertThat(testObj.getShopForId(SHOP_ID)).isEqualTo(shopData);
  }

  @Test(expected = UnknownIdentifierException.class)
  public void getShopWhenIdDoesNotExist() {
    when(shopService.getShopForId(SHOP_ID)).thenReturn(null);
    testObj.getShopForId(SHOP_ID);
  }

  @Test
  public void getShopEvaluationsWhenTheyExist() {
    EvaluationPageData output = testObj.getShopEvaluationPage(SHOP_ID, pageableData);
    verify(evaluationDataConverter).convertAll(anyCollectionOf(MiraklEvaluation.class));
    assertThat(output.getEvaluationPageCount()).isEqualTo(5);
    assertThat(output.getEvaluations()).containsExactly(evaluationData);
  }

}
