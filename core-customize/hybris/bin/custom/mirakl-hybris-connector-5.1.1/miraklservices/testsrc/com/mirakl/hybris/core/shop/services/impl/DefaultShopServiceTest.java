package com.mirakl.hybris.core.shop.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue.MiraklNumericAdditionalFieldValue;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.shop.MiraklGetShopEvaluationsRequest;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.daos.ShopDao;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultShopServiceTest {

  public static final String SHOP_ID = "shop_id";
  public static final int EVALUATION_PAGE = 5;
  public static final int EVALUATION_PAGE_SIZE = 10;
  public static final int WRONG_EVALUATION_PAGE = -3;
  private static final String CUSTOM_FIELDS_JSON = "custom-fields-json";

  @InjectMocks
  private DefaultShopService testObj;
  @Mock
  private ShopDao shopDao;
  @Mock
  private MiraklMarketplacePlatformFrontApi mockedMiraklApi;
  @Mock
  private PageableData pageableData;
  @Mock
  private ShopModel shopModel;
  @Mock
  private ModelService modelService;
  @Mock
  private JsonMarshallingService jsonMarshallingService;

  private List<MiraklAdditionalFieldValue> customFields =
      new ArrayList<>(Collections.singletonList(mock(MiraklNumericAdditionalFieldValue.class)));

  @Before
  public void setUp() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    when(pageableData.getCurrentPage()).thenReturn(EVALUATION_PAGE);
    when(pageableData.getPageSize()).thenReturn(EVALUATION_PAGE_SIZE);
  }

  @Test
  public void getShopForIdShouldReturnAResult() {
    when(shopDao.findShopById(anyString())).thenReturn(new ShopModel());

    assertNotNull(testObj.getShopForId(SHOP_ID));
  }

  @Test
  public void getShopForIdShouldReturnNullWhenNoResult() {
    when(shopDao.findShopById(anyString())).thenReturn(null);

    assertNull(testObj.getShopForId(SHOP_ID));
  }

  @Test
  public void getEvaluationsInNormalUse() {
    ArgumentCaptor<MiraklGetShopEvaluationsRequest> argument = ArgumentCaptor.forClass(MiraklGetShopEvaluationsRequest.class);

    testObj.getEvaluations(SHOP_ID, pageableData);

    verify(mockedMiraklApi).getShopEvaluations(argument.capture());
    assertThat(argument.getValue().getOffset()).isEqualTo(40);
    assertThat(argument.getValue().getMax()).isEqualTo(EVALUATION_PAGE_SIZE);
    assertThat(argument.getValue().isPaginate()).isTrue();
    assertThat(argument.getValue().getShopId()).isEqualTo(SHOP_ID);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getEvaluationsWhenWrongPageIsRequested() {
    when(pageableData.getCurrentPage()).thenReturn(WRONG_EVALUATION_PAGE);

    testObj.getEvaluations(SHOP_ID, pageableData);
  }

  public void storeShopCustomFields() {

    testObj.storeShopCustomFields(customFields, shopModel);

    verify(shopModel).setCustomFieldsJSON(CUSTOM_FIELDS_JSON);
    verify(modelService).save(shopModel);
  }

  public void shouldLOadShopCustomFields() {
    when(shopModel.getCustomFieldsJSON()).thenReturn(CUSTOM_FIELDS_JSON);

    List<MiraklAdditionalFieldValue> shopCustomField = testObj.loadShopCustomFields(shopModel);

    assertThat(shopCustomField).isNotNull();
  }

  @Test
  public void loadShopCustomFieldsReturnNullIfNotPresent() {
    when(shopModel.getCustomFieldsJSON()).thenReturn(null);

    List<MiraklAdditionalFieldValue> shopCustomField = testObj.loadShopCustomFields(shopModel);

    assertThat(shopCustomField).isNull();
  }

}
