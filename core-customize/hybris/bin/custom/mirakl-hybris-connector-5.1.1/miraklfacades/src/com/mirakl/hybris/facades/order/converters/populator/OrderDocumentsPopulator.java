package com.mirakl.hybris.facades.order.converters.populator;

import static com.google.common.base.Predicates.instanceOf;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.mirakl.client.mmp.domain.order.document.MiraklOrderDocument;
import com.mirakl.hybris.beans.DocumentData;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.order.services.MiraklDocumentService;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class OrderDocumentsPopulator implements Populator<OrderModel, OrderData> {

  private static final Logger LOG = Logger.getLogger(OrderDocumentsPopulator.class);

  protected MiraklDocumentService documentService;
  protected Converter<MiraklOrderDocument, DocumentData> marketplaceConsignmentDocumentConverter;

  @Override
  public void populate(OrderModel source, OrderData target) throws ConversionException {
    try {
      Collection<String> marketplaceConsignmentCodes = getMarketplaceConsignmentCodes(source);
      Set<MiraklOrderDocument> documents = documentService.getDocumentsForMarketplaceConsignments(marketplaceConsignmentCodes);
      for (ConsignmentData consignmentData : target.getConsignments()) {
        Collection<MiraklOrderDocument> documentsForConsignment = getDocumentsForConsignment(consignmentData.getCode(), documents);
        consignmentData.setDocuments(marketplaceConsignmentDocumentConverter.convertAll(documentsForConsignment));
      }
    } catch (Exception e) {
      LOG.error(String.format("Impossible to load documents for order [%s]", source.getCode()), e);
    }
  }

  protected Collection<MiraklOrderDocument> getDocumentsForConsignment(final String consignmentCode,
      Set<MiraklOrderDocument> documents) {
    return Collections2.filter(documents, new Predicate<MiraklOrderDocument>() {
      @Override public boolean apply(MiraklOrderDocument miraklOrderDocument) {
        return consignmentCode.equals(miraklOrderDocument.getOrderId());
      }
    });
  }

  protected Collection<String> getMarketplaceConsignmentCodes(OrderModel order) {
    FluentIterable<String> marketplaceConsignments = FluentIterable.from(order.getConsignments())
        .filter(instanceOf(MarketplaceConsignmentModel.class)).transform(new Function<ConsignmentModel, String>() {
          @Override public String apply(ConsignmentModel consignmentModel) {
            return consignmentModel.getCode();
          }
        });
    return marketplaceConsignments.toList();
  }

  @Required
  public void setDocumentService(MiraklDocumentService documentService) {
    this.documentService = documentService;
  }

  @Required
  public void setMarketplaceConsignmentDocumentConverter(Converter<MiraklOrderDocument, DocumentData> marketplaceConsignmentDocumentConverter) {
    this.marketplaceConsignmentDocumentConverter = marketplaceConsignmentDocumentConverter;
  }
}
