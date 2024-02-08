package com.mirakl.hybris.core.order.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class EntryMergeFilterMiraklProductTest {

  private static final String OFFER_ID = "OFFER_ID";

  @InjectMocks
  private EntryMergeFilterMiraklProduct testObj;
  @Mock
  private AbstractOrderEntryModel candidate, target;

  @Before
  public void setUp() {}

  @Test
  public void shouldAllowMergeIfBothHasOfferId() {
    when(candidate.getOfferId()).thenReturn(OFFER_ID);
    when(target.getOfferId()).thenReturn(OFFER_ID);

    Boolean result = testObj.apply(candidate, target);

    assertThat(result).isTrue();
  }

  @Test
  public void shouldAllowMergeIfNoneHasOfferId() {
    Boolean result = testObj.apply(candidate, target);

    assertThat(result).isTrue();
  }

  @Test
  public void shouldNotMergeIfOnlyCandidateHasOfferId() {
    when(candidate.getOfferId()).thenReturn(OFFER_ID);

    Boolean result = testObj.apply(candidate, target);

    assertThat(result).isFalse();
  }

  @Test
  public void shouldNotMergeIfOnlyTargetHasOfferId() {
    when(target.getOfferId()).thenReturn(OFFER_ID);

    Boolean result = testObj.apply(candidate, target);

    assertThat(result).isFalse();
  }

}
