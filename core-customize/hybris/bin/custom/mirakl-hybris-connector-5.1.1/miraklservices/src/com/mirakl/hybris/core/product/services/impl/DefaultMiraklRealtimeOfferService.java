package com.mirakl.hybris.core.product.services.impl;

import com.mirakl.client.mmp.domain.offer.MiraklOffer;
import com.mirakl.client.mmp.domain.product.MiraklOfferOnProduct;
import com.mirakl.client.mmp.domain.product.MiraklProductWithOffers;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.product.MiraklGetOffersOnProductsRequest;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;
import com.mirakl.hybris.core.product.factories.MiraklGetOfferRequestFactory;
import com.mirakl.hybris.core.product.factories.MiraklGetOffersOnProductsRequestFactory;
import com.mirakl.hybris.core.product.services.MiraklRealtimeOfferService;
import com.mirakl.hybris.core.product.strategies.MiraklRealtimeOffersFilteringStrategy;
import com.mirakl.hybris.core.util.PaginationUtils;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.primitives.Ints.checkedCast;
import static com.mirakl.hybris.core.util.PaginationUtils.getNumberOfPages;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * This default implementation calls the real-time P11 API to return the list of offers.
 */
public class DefaultMiraklRealtimeOfferService implements MiraklRealtimeOfferService {
  private static final Logger LOG = Logger.getLogger(DefaultMiraklRealtimeOfferService.class);

  protected MiraklMarketplacePlatformFrontApi miraklFrontApi;
  protected MiraklGetOffersOnProductsRequestFactory offersOnProductsRequestFactory;
  protected MiraklGetOfferRequestFactory offerRequestFactory;
  protected MiraklRealtimeOffersFilteringStrategy realtimeOffersFilteringStrategy;
  protected ConfigurationService configurationService;

  @Override
  public Map<String, List<MiraklOfferOnProduct>> getOffersForProductCodes(final List<String> productCodes) {
    validateParameterNotNullStandardMessage("productCodes", productCodes);
    int nbProductsRequested = productCodes.size();
    Map<String, MiraklProductWithOffers> productsWithOffers = new HashMap<>(productCodes.size());
    Integer numberOfCallsNeededForThisProduct;
    int maxNumberOfCallsNeeded = 0;

    String productSku;
    int page = 0;
    Map<String, Integer> callsNeededByProduct = new HashMap<>(nbProductsRequested);

    List<String> currentProductCodes = new ArrayList<>(realtimeOffersFilteringStrategy.getFilteredProductCodes(productCodes));
    try {
      while (isNotEmpty(currentProductCodes) && page <= maxNumberOfCallsNeeded) {
        // Perform the P11 call
        final MiraklGetOffersOnProductsRequest offersOnProductRequest = offersOnProductsRequestFactory.buildProductsOfferSearchRequest(currentProductCodes);
        final List<MiraklProductWithOffers> paginatedOffers =
            new ArrayList<>(miraklFrontApi.getOffersOnProducts(paginatedGetOffersRequest(offersOnProductRequest, page)));
        MiraklProductWithOffers currentProductWithOffers;
        // For each product, add the retrieved offers to the current list
        for (MiraklProductWithOffers productWithOffers : paginatedOffers) {
          productSku = productWithOffers.getProduct().getSku();

          currentProductWithOffers = productsWithOffers.get(productSku);
          if (currentProductWithOffers == null) {
            productsWithOffers.put(productSku, productWithOffers);
          } else {
            currentProductWithOffers
                .setOffers(ListUtils.union(ListUtils.emptyIfNull(currentProductWithOffers.getOffers()), productWithOffers.getOffers()));
            productsWithOffers.put(productSku, currentProductWithOffers);
          }

          numberOfCallsNeededForThisProduct = callsNeededByProduct.get(productSku);

          // The first time around, for each product, compute the number of calls that will be needed to fetch all the offers
          if (numberOfCallsNeededForThisProduct == null) {
            // page indexing starts at 0, calls counting starts at 1
            numberOfCallsNeededForThisProduct = getNumberOfPages(checkedCast(productWithOffers.getTotalCount()), getMaxResultsByPage()) - 1;
            callsNeededByProduct.put(productSku, numberOfCallsNeededForThisProduct);
            maxNumberOfCallsNeeded = Math.max(maxNumberOfCallsNeeded, numberOfCallsNeededForThisProduct);
          }

          // When the required number of calls is reached, the product is removed from the next call
          if (page >= numberOfCallsNeededForThisProduct) {
            currentProductCodes.remove(productSku);
          }
        }
        // Prepare the next call: The request is updated with the relevant products, the page index is incremented
        offersOnProductRequest.setProductIds(currentProductCodes);
        page++;
      }

      // Filter the offers
      Map<String, List<MiraklOfferOnProduct>> productsWithFilteredOffers = productsWithOffers.entrySet().stream().collect(
          Collectors.toMap(Map.Entry::getKey,
              oneProductWithItsOffers -> realtimeOffersFilteringStrategy.getFilteredOffers(oneProductWithItsOffers.getValue())));

      //Add an empty result for products that were not found.
      return productCodes.stream().collect(
          Collectors.toMap(productCode -> productCode, productCode -> productsWithFilteredOffers.getOrDefault(productCode, new ArrayList<>())));
    } catch (Exception e) {
      LOG.warn(String.format("Could not retrieve offers for products [%s], an error occurred : [%s]", productCodes, e.getMessage()), e);
    }
    return Collections.emptyMap();
  }

  @Override
  public MiraklOffer getOffer(String offerId) {
    validateParameterNotNullStandardMessage("offerId", offerId);
    return miraklFrontApi.getOffer(offerRequestFactory.buildGetOfferRequest(offerId));
  }


  protected MiraklGetOffersOnProductsRequest paginatedGetOffersRequest(final MiraklGetOffersOnProductsRequest getOffersRequest, int pageNumber) {
    return PaginationUtils.applyMiraklPagination(getOffersRequest, getMaxResultsByPage(), pageNumber * getMaxResultsByPage());
  }

  protected int getMaxResultsByPage() {
    return configurationService.getConfiguration().getInt(MiraklservicesConstants.REALTIME_OFFER_PRICE_FETCH_PAGESIZE, 100);
  }

  @Required
  public void setConfigurationService(final ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setRealtimeOffersFilteringStrategy(MiraklRealtimeOffersFilteringStrategy realtimeOffersFilteringStrategy) {
    this.realtimeOffersFilteringStrategy = realtimeOffersFilteringStrategy;
  }

  @Required
  public void setOffersOnProductsRequestFactory(MiraklGetOffersOnProductsRequestFactory offersOnProductsRequestFactory) {
    this.offersOnProductsRequestFactory = offersOnProductsRequestFactory;
  }

  @Required
  public void setOfferRequestFactory(MiraklGetOfferRequestFactory offerRequestFactory) {
    this.offerRequestFactory = offerRequestFactory;
  }

  @Required
  public void setMiraklFrontApi(MiraklMarketplacePlatformFrontApi miraklFrontApi) {
    this.miraklFrontApi = miraklFrontApi;
  }
}
