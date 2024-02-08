package com.mirakl.hybris.facades.order.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.request.order.evaluation.MiraklCreateOrderEvaluation;
import com.mirakl.hybris.beans.EvaluationData;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMarketplaceConsignmentFacadeTest {

  private static final String CONSIGNMENT_CODE = "1234a7984-45687e6541-12354987-A";
  private static final String CONSIGNMENT_ENTRY_CODE = "1234a7984-45687e6541-12354987-A-1";

  @Mock
  private Converter<EvaluationData, MiraklCreateOrderEvaluation> converter;

  @Mock
  private EvaluationData evaluation;

  @Mock
  private UserModel user;

  @Mock
  private MarketplaceConsignmentService consignmentService;

  @Mock
  private MiraklCreateOrderEvaluation convertedEvaluation;

  @Mock
  private Converter<ProductModel, ProductData> productDataConverter;

  @Mock
  private Converter<MarketplaceConsignmentModel, ConsignmentData> consignmentConverter;

  @Mock
  private ProductModel product;

  @Mock
  private ProductData productData;

  @InjectMocks
  private DefaultMarketplaceConsignmentFacade testObj;

  @Before
  public void setUp() {
    when(converter.convert(any(EvaluationData.class))).thenReturn(convertedEvaluation);
    when(consignmentService.getProductForConsignmentEntry(CONSIGNMENT_ENTRY_CODE)).thenReturn(product);
    when(productDataConverter.convert(product)).thenReturn(productData);
  }

  @Test
  public void postEvaluation() throws Exception {
    testObj.postEvaluation(CONSIGNMENT_CODE, evaluation, user);

    verify(converter).convert(any(EvaluationData.class));
    verify(consignmentService).postEvaluation(CONSIGNMENT_CODE, convertedEvaluation, user);
  }

  @Test
  public void getProductFromConsignmentEntry() {
    ProductData output = testObj.getProductForConsignmentEntry(CONSIGNMENT_ENTRY_CODE);

    verify(consignmentService).getProductForConsignmentEntry(CONSIGNMENT_ENTRY_CODE);
    verify(productDataConverter).convert(any(ProductModel.class));
    assertThat(output).isEqualTo(productData);
  }
}
