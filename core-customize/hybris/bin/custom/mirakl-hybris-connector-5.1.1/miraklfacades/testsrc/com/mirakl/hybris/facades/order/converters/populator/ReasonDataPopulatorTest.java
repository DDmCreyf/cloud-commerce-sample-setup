package com.mirakl.hybris.facades.order.converters.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.reason.MiraklReason;
import com.mirakl.hybris.beans.ReasonData;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReasonDataPopulatorTest {

  public static final String REASON_LABEL = "The product is damaged";
  public static final String REASON_CODE = "4";
  @Mock
  private MiraklReason miraklReason;

  @InjectMocks
  ReasonDataPopulator testObj;

  @Test
  public void populate() {
    when(miraklReason.getCode()).thenReturn(REASON_CODE);
    when(miraklReason.getLabel()).thenReturn(REASON_LABEL);
    ReasonData reasonData = new ReasonData();

    testObj.populate(miraklReason, reasonData);

    assertThat(reasonData.getCode()).isEqualTo(REASON_CODE);
    assertThat(reasonData.getLabel()).isEqualTo(REASON_LABEL);
  }
}
