package com.mirakl.hybris.advancedpricing.product.strategies.impl;

import static com.mirakl.hybris.core.utils.assertions.MiraklAssertions.assertThatExtractedFields;
import static com.mirakl.hybris.core.utils.assertions.Tuple.tuple;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.fest.util.Arrays;

import com.mirakl.hybris.advancedpricing.product.daos.OfferPricingDao;
import com.mirakl.hybris.advancedpricing.product.utils.OfferPricingKey;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.model.OfferPricingModel;
import com.mirakl.hybris.core.product.jobs.AbstractImportAsyncOffersIntegrationTest;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.core.utils.assertions.Tuple;

public abstract class AbstractAdvancedPricingImportIntegrationTest extends AbstractImportAsyncOffersIntegrationTest {

  @Resource
  protected OfferPricingDao offerPricingDao;
  @Resource
  protected JsonMarshallingService jsonMarshallingService;

  protected void verifyPricesDetails( //
      Collection<OfferPricingModel> pricingCollection, //
      String price, //
      String discountPrice, //
      Date discountStart, //
      Date discountEnd, //
      Tuple... volumePricingTuples //
  ) { //
    for (OfferPricingModel pricing : pricingCollection) {
      verifyPriceDetails(pricing, price, discountPrice, discountStart, discountEnd, volumePricingTuples);
    }
  }

  protected void verifyPriceDetails( //
      OfferPricingModel pricing, //
      String price, //
      String discountPrice, //
      Date discountStart, //
      Date discountEnd, //
      Tuple... volumePricingTuples //
  ) { //
    OfferPricingData priceDetails = jsonMarshallingService.fromJson(pricing.getPriceDetailsJSON(), OfferPricingData.class);
    assertThat(priceDetails.getPrice()).isEqualByComparingTo(new BigDecimal(price));
    assertThat(priceDetails.getUnitOriginPrice()).isEqualByComparingTo(new BigDecimal(price));
    if (discountPrice != null) {
      assertThat(priceDetails.getUnitDiscountPrice()).isEqualByComparingTo(new BigDecimal(discountPrice));
    } else {
      assertThat(priceDetails.getUnitDiscountPrice()).isNull();
    }
    assertThat(priceDetails.getDiscountStartDate()).isEqualTo(discountStart);
    assertThat(priceDetails.getDiscountEndDate()).isEqualTo(discountEnd);
    assertThat(priceDetails.getChannelCode()).isEqualTo(pricing.getChannelCode());
    List<OfferVolumePricingData> volumePrices = priceDetails.getVolumePrices();
    if (Arrays.isEmpty(volumePricingTuples)) {
      assertThatExtractedFields(volumePrices, "price", "unitOriginPrice", "unitDiscountPrice", "quantityThreshold") //
          .containsExactly(tuple(toBigDecimal(price), toBigDecimal(price), toBigDecimal(discountPrice), 1));
      if (isNotEmpty(volumePricingTuples)) {
        assertThatExtractedFields(volumePrices, "price", "unitOriginPrice", "unitDiscountPrice", "quantityThreshold") //
            .containsExactly(volumePricingTuples);
      }
    }
  }

  protected OfferPricingModel getPricing(OfferPricingKey pricingKey) {
    List<OfferPricingModel> pricingList = offerPricingDao.findByOfferId(pricingKey.getOfferId());
    List<OfferPricingModel> output = new ArrayList<>();
    for (OfferPricingModel pricing : pricingList) {
      if (pricingKey.equals(new OfferPricingKey(pricing))) {
        output.add(pricing);
      }
    }
    assertThat(output).hasSize(1);
    return output.get(0);
  }

  protected BigDecimal toBigDecimal(String value) {
    return isBlank(value) ? null : new BigDecimal(value);
  }
}
