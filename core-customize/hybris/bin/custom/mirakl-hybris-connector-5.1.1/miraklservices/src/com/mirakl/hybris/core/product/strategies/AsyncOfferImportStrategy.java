package com.mirakl.hybris.core.product.strategies;

import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOffer;
import com.mirakl.hybris.core.model.OfferModel;

/**
 * Updates Hybris' {@link OfferModel}s using Mirakl's OF54 {@link MiraklAsyncExportOffer} data
 */
public interface AsyncOfferImportStrategy {

    /**
     * Creates or updates the {@link OfferModel} from Mirakl's {@link MiraklAsyncExportOffer}.
     * If advanced pricing is installed, also handles the advanced pricing data.
     * Note : This method also handles offer invalidation on delta offer import files.
     *
     * @param offer the offer data from Mirakl
     */
    void importMiraklOffer(MiraklAsyncExportOffer offer);

    /**
     * Invalidates the offer by changing the value of the 'active' and 'deleted' flags.
     * If advanced pricing is installed, also erases the advanced pricing data.
     * This method is called ONLY for offers existing in hybris but missing from a full offer import file.
     *
     * @param offer the offer data from Mirakl
     */
    void invalidateHybrisOffer(OfferModel offer);
}
