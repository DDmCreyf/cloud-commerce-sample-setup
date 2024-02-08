package com.mirakl.hybris.core.product.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultOfferCodeGenerationStrategyTest {

  private static final String PREFIX = "OFFER_PREFIX_";
  private static final String OFFER_ID = "offer-id";

  @Spy
  @InjectMocks
  private DefaultOfferCodeGenerationStrategy codeGenerationStrategy;

  @Before
  public void setUp() throws Exception {
    codeGenerationStrategy.setOfferCodePrefix(PREFIX);
  }

  @Test
  public void shouldGenerateOfferCode() {
    String internalCode = codeGenerationStrategy.generateCode(OFFER_ID);

    assertThat(internalCode).isEqualTo(PREFIX + OFFER_ID);
  }

  @Test
  public void shouldMatchOnCorrectOfferCodePattern() {
    boolean isOfferCode = codeGenerationStrategy.isOfferCode(PREFIX + "SomeString");

    assertThat(isOfferCode).isTrue();
  }

  @Test
  public void shouldNotMatchOnIncorrectOfferCodePattern() {
    boolean isOfferCode = codeGenerationStrategy.isOfferCode("SomeString" + PREFIX + "SomeOtherString");

    assertThat(isOfferCode).isFalse();
  }

  @Test
  public void shouldTranslateCodeToIdWhenMatchesOfferCodePattern() {
    String offerId = codeGenerationStrategy.translateCodeToId(PREFIX + OFFER_ID);

    assertThat(offerId).isEqualTo(OFFER_ID);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionWhenNotMatchingPattern() {
    String notMatchingCode = "not-matching-pattern-input";
    doReturn(false).when(codeGenerationStrategy).isOfferCode(notMatchingCode);

    codeGenerationStrategy.translateCodeToId(notMatchingCode);
  }

}
