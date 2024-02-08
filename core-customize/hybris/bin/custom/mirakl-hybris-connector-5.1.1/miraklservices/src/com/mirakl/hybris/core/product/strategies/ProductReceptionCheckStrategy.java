package com.mirakl.hybris.core.product.strategies;

import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;

public interface ProductReceptionCheckStrategy {

    /**
     * Verify if a raw product is already present as is in the database.
     *
     * @param rawProduct raw product to check
     * @param context product import file context
     * @return true if the data of the raw product is identical to the data in the database
     */
    boolean isAlreadyReceived(MiraklRawProductModel rawProduct, ProductImportFileContextData context);

    /**
     * Method to run when isAlreadyReceived is positive.
     * Handles the raw products if they were already received as is before.
     *
     * @param rawProduct raw product
     * @param context product import file context
     */
    void handleAlreadyReceived(MiraklRawProductModel rawProduct, ProductImportFileContextData context);
}

