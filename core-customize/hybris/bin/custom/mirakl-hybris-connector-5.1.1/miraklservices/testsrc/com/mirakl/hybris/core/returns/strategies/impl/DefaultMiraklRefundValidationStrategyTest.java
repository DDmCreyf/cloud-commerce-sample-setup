package com.mirakl.hybris.core.returns.strategies.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklRefundRequestData;
import com.mirakl.hybris.core.returns.dao.MiraklRefundEntryDao;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.returns.model.RefundEntryModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklRefundValidationStrategyTest {

  private static final String REFUND_ID = "refund-id";

  @InjectMocks
  private DefaultMiraklRefundValidationStrategy refundValidationStrategy;

  @Mock
  private MiraklRefundEntryDao refundEntryDao;

  @Mock
  private MiraklRefundRequestData refundRequestData;

  @SuppressWarnings("unchecked")
  @Test
  public void shouldRefuseAlreadyReceivedRefunds() {
    when(refundRequestData.getRefundId()).thenReturn(REFUND_ID);
    when(refundEntryDao.find((Map<String, ? extends Object>) argThat(new ArgumentMatcher<Object>() {

      @Override
      public boolean matches(Object argument) {
        return REFUND_ID.equals(((Map<String, ? extends Object>) argument).get(RefundEntryModel.MIRAKLREFUNDID));
      }
    }))).thenReturn(asList(mock(RefundEntryModel.class)));

    boolean validationResult = refundValidationStrategy.isValidRefundRequest(refundRequestData);

    assertThat(validationResult).isFalse();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void shouldAllowNewRefunds() {
    when(refundRequestData.getRefundId()).thenReturn(REFUND_ID);
    when(refundEntryDao.find((Map<String, ? extends Object>) argThat(new ArgumentMatcher<Object>() {

      @Override
      public boolean matches(Object argument) {
        return REFUND_ID.equals(((Map<String, ? extends Object>) argument).get(RefundEntryModel.MIRAKLREFUNDID));
      }
    })))
    .thenReturn(Collections.<RefundEntryModel>emptyList());

    boolean validationResult = refundValidationStrategy.isValidRefundRequest(refundRequestData);

    assertThat(validationResult).isTrue();
  }
}
