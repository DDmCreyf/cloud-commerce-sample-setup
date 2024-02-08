package com.mirakl.hybris.core.product.services;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.impl.DefaultOfferService;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.util.List;
import java.util.Map;

/**
 * Service providing access and control on the Offers imported from Mirakl
 */
public interface OfferService {

  /**
   * Returns the offer with the specified id.
   *
   * @param offerId the id of the offer
   * @return the offer with the specified id.
   * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException if no Offer with the specified id is found
   */
  OfferModel getOfferForId(String offerId);


  /**
   * Returns the offer with the specified id ignoring search restrictions.
   *
   * @param offerId the id of the offer
   * @return the offer with the specified id.
   * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException if no Offer with the specified id is found
   */
  OfferModel getOfferForIdIgnoreSearchRestrictions(String offerId);

  /**
   * Update the offer with the specified id calling Mirakl (OF22).
   *
   * @param offerId the id of the offer
   * @return the offer with the specified id.
   * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException if no Offer with the specified id is found
   */
  OfferModel updateExistingOfferForId(String offerId);

  /**
   * Returns the offers for the given product code
   *
   * @param productCode The code of the product
   * @return A list of offers
   */
  List<OfferModel> getOffersForProductCode(String productCode);

  /**
   * Returns a map of offers for each product code
   *
   * @param productCodes The list of product codes for which to retrieve Offers
   * @return A map of the offers of the products identified by their code
   */
  Map<String, List<OfferModel>> getOffersForProductCodes(List<String> productCodes);

  /**
   * Returns the offers for the given product code, sorted using the {@link DefaultOfferService#sortingStrategy}
   *
   * @param productCode The code of the product
   * @return A list of sorted offers
   */
  List<OfferModel> getSortedOffersForProductCode(String productCode);

  /**
   * Returns the offers for the products whose codes are provided, sorted using the {@link DefaultOfferService#sortingStrategy}
   *
   * @param productCodes product codes
   * @return A list of sorted offers
   */
  Map<String, List<OfferModel>> getSortedOffersForProductCodes(List<String> productCodes);


  /**
   * Checks if there are any offers associated with the product code. Search restrictions apply.
   *
   * @param productCode unique code of the product
   * @return true if any offers found
   */
  boolean hasOffers(String productCode);

  /**
   * Checks if there are any offers associated with the product code. Search restrictions apply.
   *
   * @param productCode unique code of the product
   * @param currency the currency of the searched offers
   * @return true if any offers found
   */
  boolean hasOffersWithCurrency(String productCode, CurrencyModel currency);

  /**
   * Returns the offers quantity associated with the product code. Search restrictions apply.
   *
   * @param productCode unique code of the product
   * @return The total quantity of offers
   */
  int countOffersForProduct(String productCode);

  /**
   * Marshals all offer pricings and stores them as JSON into the Offer model.
   *
   * @param allOfferPricings all channels offer pricings
   * @param offer the offer on which to store
   */
  void storeAllOfferPricings(List<OfferPricingData> allOfferPricings, OfferModel offer);

  /**
   * Unmarshals the stored offer pricings JSON from the Offer model and calculates the applicable prices (taking into account the
   * discount dates)
   *
   * @param offer the offer
   * @return the list of stored offer pricings
   */
  List<OfferPricingData> loadAllOfferPricings(OfferModel offer);

  /**
   * Unmarshals the offer pricings JSON from {@link OfferOverviewData} and calculates the applicable prices (taking into account
   * the discount dates)
   *
   * @param offer
   * @return
   */
  List<OfferPricingData> loadAllOfferPricings(OfferOverviewData offer);

  /**
   * Store offer custom fields.
   *
   * @param customFields the offer custom fields
   * @param offer the offer
   */
  void storeOfferCustomFields(List<MiraklAdditionalFieldValue> customFields, OfferModel offer);

  /**
   * Load offer custom fields.
   *
   * @param offer the offer
   * @return the offer custom fields
   */
  List<MiraklAdditionalFieldValue> loadOfferCustomFields(OfferModel offer);
}
