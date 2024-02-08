package com.mirakl.hybris.core.product.strategies;

public interface OfferCodeGenerationStrategy {

  /**
   * Given an offerId, this method generates an internal offer code.
   * 
   * @param offerId
   * @return
   */
  String generateCode(String offerId);


  /**
   * Takes a generated offer code and translate it back to its origin offerId
   * 
   * @param offerCode the offerCode to be translated to an offerId
   * @return the translated offerId if the input is recongnized as an offer code. Throws an {@link IllegalArgumentException} if
   *         the given offerCode does not match the offer codes pattern
   */
  String translateCodeToId(String offerCode);


  /**
   * Returns true if a given string corresponds to a format of an internal offer code.
   * 
   * @param code
   * @return
   */
  boolean isOfferCode(String code);


}
