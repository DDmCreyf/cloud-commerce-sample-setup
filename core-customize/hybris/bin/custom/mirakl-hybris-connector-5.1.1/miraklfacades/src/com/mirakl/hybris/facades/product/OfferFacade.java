package com.mirakl.hybris.facades.product;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.core.model.OfferModel;

import java.util.List;
import java.util.Map;

/**
 *
 * The offer facade get the offers core information ready to be displayed on a front.
 */
public interface OfferFacade {

  /**
   * Get filtered and sorted Offers for the designated product code
   *
   * @param productCode the product code of the offer wanted list
   * @return a list containing the offers
   */
  List<OfferData> getOffersForProductCode(String productCode);

  /**
   * Get filtered and sorted Offers for the products whose code is passed as parameter
   *
   * @param productCodes list of product code for which offers will be retrieved
   * @return a map containing, for each provided product, the offers
   */
  Map<String, List<OfferData>> getOffersForProductCodes(List<String> productCodes);


  /**
   * Returns the offer with the specified operator code.
   *
   * @param offerCode the code of the offer
   * @return the offer corresponding to the specified code.
   * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException if no Offer with the specified code is found
   */
  OfferModel getOfferForCode(String offerCode);

  /**
   * Returns the offer ignoring search restrictions with the specified operator code.
   *
   * @param offerCode the code of the offer
   * @return the offer corresponding to the specified code.
   * @throws de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException if no Offer with the specified code is found
   */
  OfferModel getOfferForCodeIgnoreSearchRestrictions(String offerCode);
}
