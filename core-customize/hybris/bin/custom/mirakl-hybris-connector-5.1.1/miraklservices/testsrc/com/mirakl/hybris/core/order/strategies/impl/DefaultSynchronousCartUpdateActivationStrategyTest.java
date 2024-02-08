package com.mirakl.hybris.core.order.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSynchronousCartUpdateActivationStrategyTest {

  @Spy
  @InjectMocks
  private DefaultSynchronousCartUpdateActivationStrategy testObj;

  @Mock
  private BaseStoreService baseStoreService;
  @Mock
  private BaseStoreModel baseStore;

  @Before
  public void setUp() throws Exception {
    when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
  }

  @Test
  public void isSynchronousCartUpdateEnabledShouldReturnTrueIfEnabledInTheBaseStore() {
    when(baseStore.isSynchronousCartUpdateEnabled()).thenReturn(true);

    assertThat(testObj.isSynchronousCartUpdateEnabled()).isTrue();
  }

  @Test
  public void isSynchronousCartUpdateEnabledShouldReturnFalseIfNotEnabledInTheBaseStore() {
    when(baseStore.isSynchronousCartUpdateEnabled()).thenReturn(false);

    assertThat(testObj.isSynchronousCartUpdateEnabled()).isFalse();
  }
}
