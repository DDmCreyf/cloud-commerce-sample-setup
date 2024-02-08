package com.mirakl.hybris.facades.shop.converters.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.ShopData;
import com.mirakl.hybris.core.model.ShopModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(value = MockitoJUnitRunner.class)
public class ShopDataPopulatorTest {

  public static final long SHOP_APPROVAL_DELAY = 1645;
  public static final double SHOP_APPROVAL_RATE = 0.4;
  public static final String SHOP_DESCRIPTION = "shop_description";
  public static final int SHOP_EVALUATION_COUNT = 978;
  public static final double SHOP_GRADE = 4.5;
  public static final String SHOP_ID = "shop_id";
  public static final String SHOP_NAME = "shop_name";
  public static final boolean SHOP_PREMIUM_STATE = false;
  public static final Date SHOP_REGISTRATION_DATE = new Date();
  public static final String SHOP_RETURN_POLICY = "shop_return_policy";
  public static final String SHOP_MEDIA_URL = "shop_media_url";
  public static final String SHOP_COUNTRY = "shop_country";

  @Mock
  private CountryModel mockedCountry;
  @Mock
  private ShopModel mockedShopModel;
  @Mock
  private MediaModel mockedMedia;
  @Mock
  private SearchStateData searchStateData;
  @Mock
  Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;

  @InjectMocks
  private ShopDataPopulator testObj;

  @Before
  public void setUp() {

    when(mockedMedia.getURL()).thenReturn(SHOP_MEDIA_URL);
    when(mockedCountry.getName()).thenReturn(SHOP_COUNTRY);

    when(mockedShopModel.getApprovalDelay()).thenReturn(SHOP_APPROVAL_DELAY);
    when(mockedShopModel.getApprovalRate()).thenReturn(SHOP_APPROVAL_RATE);
    when(mockedShopModel.getDescription()).thenReturn(SHOP_DESCRIPTION);
    when(mockedShopModel.getEvaluationCount()).thenReturn(SHOP_EVALUATION_COUNT);
    when(mockedShopModel.getGrade()).thenReturn(SHOP_GRADE);
    when(mockedShopModel.getId()).thenReturn(SHOP_ID);
    when(mockedShopModel.getName()).thenReturn(SHOP_NAME);
    when(mockedShopModel.getPremium()).thenReturn(SHOP_PREMIUM_STATE);
    when(mockedShopModel.getRegistrationDate()).thenReturn(SHOP_REGISTRATION_DATE);
    when(mockedShopModel.getReturnPolicy()).thenReturn(SHOP_RETURN_POLICY);
    when(mockedShopModel.getShippingCountry()).thenReturn(mockedCountry);
    when(mockedShopModel.getBanner()).thenReturn(mockedMedia);
    when(mockedShopModel.getLogo()).thenReturn(mockedMedia);
    when(solrSearchStateConverter.convert(any(SolrSearchQueryData.class))).thenReturn(searchStateData);
  }

  @Test
  public void populateTest() {
    ShopData output = new ShopData();
    testObj.populate(mockedShopModel, output);

    assertThat(output.getApprovalDelay()).isEqualTo(SHOP_APPROVAL_DELAY);
    assertThat(output.getApprovalRate()).isEqualTo(SHOP_APPROVAL_RATE);
    assertThat(output.getBanner()).isEqualTo(SHOP_MEDIA_URL);
    assertThat(output.getDescription()).isEqualTo(SHOP_DESCRIPTION);
    assertThat(output.getEvaluationCount()).isEqualTo(SHOP_EVALUATION_COUNT);
    assertThat(output.getGrade()).isEqualTo(SHOP_GRADE);
    assertThat(output.getId()).isEqualTo(SHOP_ID);
    assertThat(output.getLogo()).isEqualTo(SHOP_MEDIA_URL);
    assertThat(output.getName()).isEqualTo(SHOP_NAME);
    assertThat(output.getPremium()).isEqualTo(SHOP_PREMIUM_STATE);
    assertThat(output.getRegistrationDate()).isEqualTo(SHOP_REGISTRATION_DATE);
    assertThat(output.getReturnPolicy()).isEqualTo(SHOP_RETURN_POLICY);
    assertThat(output.getShippingCountry()).isEqualTo(SHOP_COUNTRY);
    assertThat(output.getOffersPageUrl()).isEqualTo(searchStateData.getUrl());
  }
}
