package com.mirakl.hybris.facades.order.impl;

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

import com.mirakl.client.mmp.domain.evaluation.MiraklAssessment;
import com.mirakl.hybris.beans.AssessmentData;
import com.mirakl.hybris.core.order.services.MiraklOrderService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;



/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMarketplaceOrderFacadeTest {

  @Mock
  private MiraklOrderService orderService;

  @Mock
  private Converter<MiraklAssessment, AssessmentData> converter;

  @Mock
  private MiraklAssessment miraklAssessment;

  @InjectMocks
  private DefaultMarketplaceOrderFacade testObj;

  @Before
  public void setUp() throws Exception {
    when(orderService.getAssessments()).thenReturn(Collections.singletonList(miraklAssessment));
  }

  @Test
  public void getAssessments() throws Exception {
    testObj.getAssessments();

    verify(orderService).getAssessments();
    verify(converter).convertAll(anyCollectionOf(MiraklAssessment.class));
  }
}
