package com.mirakl.hybris.core.product.populators;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.mirakl.client.mci.domain.product.MiraklProductDataSheetAcceptanceStatus;
import com.mirakl.client.mci.request.product.MiraklGetProductDataSheetsRequest;
import com.mirakl.client.request.common.MiraklContentType;
import com.mirakl.hybris.beans.ProductDataSheetDownloadParams;
import com.mirakl.hybris.core.enums.MarketplaceProductAcceptanceStatus;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DefaultMiraklGetProductDataSheetsRequestPopulator
implements Populator<ProductDataSheetDownloadParams, MiraklGetProductDataSheetsRequest> {

  @Override
  public void populate(ProductDataSheetDownloadParams source, MiraklGetProductDataSheetsRequest target)
      throws ConversionException {
    target.setAcceptanceStatuses(getAcceptanceStatuses(source.getAcceptanceStatuses()));
    target.setUpdatedSince(source.getUpdatedSince());
    target.setContentType(MiraklContentType.CSV);
    if (isNotEmpty(source.getCatalogs())) {
      target.setCatalogs(newArrayList(source.getCatalogs()));
    }

  }

  protected List<MiraklProductDataSheetAcceptanceStatus> getAcceptanceStatuses(
      Collection<MarketplaceProductAcceptanceStatus> acceptanceStatuses) {
    return new ArrayList<>(Collections2.transform(acceptanceStatuses,
        new Function<MarketplaceProductAcceptanceStatus, MiraklProductDataSheetAcceptanceStatus>() {
      @Override
      public MiraklProductDataSheetAcceptanceStatus apply(MarketplaceProductAcceptanceStatus acceptanceStatus) {
        return MiraklProductDataSheetAcceptanceStatus.valueOf(acceptanceStatus.getCode());
      }
    }));
  }

}
