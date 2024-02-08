package com.mirakl.hybris.addon.controllers.pages;

import static java.lang.Integer.parseInt;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.mirakl.hybris.addon.controllers.MirakladdonControllerConstants;
import com.mirakl.hybris.beans.EvaluationPageData;
import com.mirakl.hybris.beans.ShopData;
import com.mirakl.hybris.core.shop.services.ShopService;
import com.mirakl.hybris.facades.shop.ShopFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(value = MockitoJUnitRunner.class)
public class ShopPageControllerTest {

  public static final String SHOP_ID = "shop_id";
  public static final String SHOP_NAME = "shop_name";
  public static final String SHOP_ATTRIBUTE = "shop";
  public static final String PAGE_TITLE = "pageTitle";
  public static final String EVALUATION_PAGE_NUMBER = "1";
  public static final String CURRENT_PAGE = "currentPage";
  public static final String SHOP_INFORMATION_PAGE = "shop-information";
  public static final Integer EVALUATION_COUNT = 10;

  @InjectMocks
  private ShopPageController testObj = new ShopPageController();

  @Mock
  private CMSPageService cmsPageService;
  @Mock
  private ShopFacade shopFacade;
  @Mock
  private ShopService shopService;
  @Mock
  private ShopData shopData;
  @Mock
  private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;
  @Mock
  private EvaluationPageData evaluationPageData;

  @Before
  public void setUp() {
    when(shopFacade.getShopForId(anyString())).thenReturn(shopData);
    when(shopFacade.getShopEvaluationPage(anyString(), any(PageableData.class))).thenReturn(evaluationPageData);
    when(shopData.getName()).thenReturn(SHOP_NAME);
    when(shopData.getEvaluationCount()).thenReturn(EVALUATION_COUNT);
  }

  @Test
  public void shopDetail() throws Exception {
    Model model = new ExtendedModelMap();
    testObj.shopDetail(SHOP_ID, model, null, null);

    Map<String, Object> attributes = model.asMap();
    assertThat(attributes.get(SHOP_ATTRIBUTE)).isEqualTo(shopData);
    assertThat(attributes.get(PAGE_TITLE)).isEqualTo(SHOP_NAME);
    verify(cmsPageService).getPageForLabelOrId(SHOP_INFORMATION_PAGE);
  }

  @Test
  public void shopEvaluationsTab() throws Exception {
    Model model = new ExtendedModelMap();

    String result = testObj.shopEvaluationsTab(SHOP_ID, EVALUATION_PAGE_NUMBER, model, null, null);

    Map<String, Object> attributes = model.asMap();
    assertThat(attributes.get(ShopPageController.EVALUATION_PAGE_CONTENT)).isEqualTo(evaluationPageData);
    assertThat(attributes.get(CURRENT_PAGE)).isEqualTo(parseInt(EVALUATION_PAGE_NUMBER));
    assertThat(result).isEqualTo(MirakladdonControllerConstants.Fragments.Shop.shopEvaluationFragment);
  }
}
