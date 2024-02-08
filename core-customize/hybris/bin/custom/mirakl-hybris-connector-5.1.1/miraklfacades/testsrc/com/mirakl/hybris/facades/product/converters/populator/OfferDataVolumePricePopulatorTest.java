package com.mirakl.hybris.facades.product.converters.populator;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.model.OfferModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import org.apache.commons.lang3.tuple.Pair;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OfferDataVolumePricePopulatorTest {

  @InjectMocks
  private OfferDataVolumePricePopulator persistentOfferDataVolumePricePopulator;
  @Mock
  private PriceDataFactory priceDataFactory;
  @Mock
  private Comparator<PriceData> volumePriceComparator;
  @Mock
  private OfferModel offer;
  @Mock
  private OfferPricingData offerPricingData;
  @Mock
  private CurrencyModel currencyModel;

  @Before
  public void setup() {
    when(offer.getCurrency()).thenReturn(currencyModel);
    when(currencyModel.getIsocode()).thenReturn("TEST_CURRENCY");
  }

  @Test
  public void shouldPopulateVolumePrices() {
    List<OfferVolumePricingData> volumePrices = new ArrayList<>();
    volumePrices.add(createVolumePrice(valueOf(50), valueOf(45), 1));
    volumePrices.add(createVolumePrice(valueOf(40), valueOf(35), 5));
    volumePrices.add(createVolumePrice(valueOf(30), valueOf(25), 10));

    when(offerPricingData.getVolumePrices()).thenReturn(volumePrices);
    when(priceDataFactory.create(eq(PriceDataType.BUY), any(BigDecimal.class), any(String.class))).thenAnswer(createPriceDataAnswer());

    OfferData result = new OfferData();
    persistentOfferDataVolumePricePopulator.populate(Pair.of(offerPricingData, offer.getCurrency().getIsocode()), result);

    List<PriceData> volumePricesDatas = result.getVolumePrices();
    assertThat(volumePricesDatas).hasSize(3);
    assertThat(volumePricesDatas.get(0).getMinQuantity()).isEqualTo(1);
    assertThat(volumePricesDatas.get(0).getMaxQuantity()).isEqualTo(4);
    assertThat(volumePricesDatas.get(0).getValue()).isEqualTo((valueOf(50)));
    assertThat(volumePricesDatas.get(1).getMinQuantity()).isEqualTo(5);
    assertThat(volumePricesDatas.get(1).getMaxQuantity()).isEqualTo(9);
    assertThat(volumePricesDatas.get(1).getValue()).isEqualTo((valueOf(40)));
    assertThat(volumePricesDatas.get(2).getMinQuantity()).isEqualTo(10);
    assertThat(volumePricesDatas.get(2).getMaxQuantity()).isNull();
    assertThat(volumePricesDatas.get(2).getValue()).isEqualTo((valueOf(30)));

    List<PriceData> volumeOriginPricesDatas = result.getVolumeOriginPrices();
    assertThat(volumeOriginPricesDatas).hasSize(3);
    assertThat(volumeOriginPricesDatas.get(0).getMinQuantity()).isEqualTo(1);
    assertThat(volumeOriginPricesDatas.get(0).getMaxQuantity()).isEqualTo(4);
    assertThat(volumeOriginPricesDatas.get(0).getValue()).isEqualTo((valueOf(45)));
    assertThat(volumeOriginPricesDatas.get(1).getMinQuantity()).isEqualTo(5);
    assertThat(volumeOriginPricesDatas.get(1).getMaxQuantity()).isEqualTo(9);
    assertThat(volumeOriginPricesDatas.get(1).getValue()).isEqualTo((valueOf(35)));
    assertThat(volumeOriginPricesDatas.get(2).getMinQuantity()).isEqualTo(10);
    assertThat(volumeOriginPricesDatas.get(2).getMaxQuantity()).isNull();
    assertThat(volumeOriginPricesDatas.get(2).getValue()).isEqualTo((valueOf(25)));
  }

  @Test
  public void shouldPopulateOriginVolumePricesWhenNeeded() {
    List<OfferVolumePricingData> volumePrices = new ArrayList<>();
    volumePrices.add(createVolumePrice(valueOf(50), valueOf(45), 1));
    volumePrices.add(createVolumePrice(valueOf(40), null, 5));
    volumePrices.add(createVolumePrice(valueOf(30), null, 10));

    when(offerPricingData.getUnitOriginPrice()).thenReturn(valueOf(45));
    when(offerPricingData.getVolumePrices()).thenReturn(volumePrices);

    when(priceDataFactory.create(eq(PriceDataType.BUY), any(BigDecimal.class), any(String.class))).thenAnswer(createPriceDataAnswer());

    OfferData result = new OfferData();
    persistentOfferDataVolumePricePopulator.populate(Pair.of(offerPricingData, offer.getCurrency().getIsocode()), result);

    List<PriceData> volumePricesDatas = result.getVolumePrices();
    assertThat(volumePricesDatas).hasSize(3);
    assertThat(volumePricesDatas.get(0).getMinQuantity()).isEqualTo(1);
    assertThat(volumePricesDatas.get(0).getMaxQuantity()).isEqualTo(4);
    assertThat(volumePricesDatas.get(0).getValue()).isEqualTo((valueOf(50)));
    assertThat(volumePricesDatas.get(1).getMinQuantity()).isEqualTo(5);
    assertThat(volumePricesDatas.get(1).getMaxQuantity()).isEqualTo(9);
    assertThat(volumePricesDatas.get(1).getValue()).isEqualTo((valueOf(40)));
    assertThat(volumePricesDatas.get(2).getMinQuantity()).isEqualTo(10);
    assertThat(volumePricesDatas.get(2).getMaxQuantity()).isNull();
    assertThat(volumePricesDatas.get(2).getValue()).isEqualTo((valueOf(30)));

    List<PriceData> volumeOriginPricesDatas = result.getVolumeOriginPrices();
    assertThat(volumeOriginPricesDatas).hasSize(3);
    assertThat(volumeOriginPricesDatas.get(0).getMinQuantity()).isEqualTo(1);
    assertThat(volumeOriginPricesDatas.get(0).getMaxQuantity()).isEqualTo(4);
    assertThat(volumeOriginPricesDatas.get(0).getValue()).isEqualTo((valueOf(45)));
    assertThat(volumeOriginPricesDatas.get(1).getMinQuantity()).isEqualTo(5);
    assertThat(volumeOriginPricesDatas.get(1).getMaxQuantity()).isEqualTo(9);
    assertThat(volumeOriginPricesDatas.get(1).getValue()).isEqualTo((valueOf(45)));
    assertThat(volumeOriginPricesDatas.get(2).getMinQuantity()).isEqualTo(10);
    assertThat(volumeOriginPricesDatas.get(2).getMaxQuantity()).isNull();
    assertThat(volumeOriginPricesDatas.get(2).getValue()).isEqualTo((valueOf(45)));
  }


  @Test
  public void shouldNotPopulateVolumePricesIfLessThan2() {
    List<OfferVolumePricingData> volumePrices = new ArrayList<>();

    volumePrices.add(createVolumePrice(valueOf(86), valueOf(47), 1));
    when(offerPricingData.getVolumePrices()).thenReturn(volumePrices);

    OfferData result = new OfferData();
    persistentOfferDataVolumePricePopulator.populate(Pair.of(offerPricingData, offer.getCurrency().getIsocode()), result);
    Assertions.assertThat(result.getVolumePrices()).isNull();

  }

  protected OfferVolumePricingData createVolumePrice(BigDecimal price, BigDecimal unitOriginPrice, int quantityThreshold) {
    OfferVolumePricingData volumePrice = new OfferVolumePricingData();
    volumePrice.setPrice(price);
    volumePrice.setUnitOriginPrice(unitOriginPrice);
    volumePrice.setQuantityThreshold(quantityThreshold);
    return volumePrice;
  }

  protected Answer<PriceData> createPriceDataAnswer() {
    return invocation -> {
      Object[] args = invocation.getArguments();
      final PriceData priceData = new PriceData();
      priceData.setPriceType((PriceDataType) args[0]);
      priceData.setValue((BigDecimal) args[1]);
      priceData.setCurrencyIso(((String) args[2]));
      return priceData;
    };
  }
}
