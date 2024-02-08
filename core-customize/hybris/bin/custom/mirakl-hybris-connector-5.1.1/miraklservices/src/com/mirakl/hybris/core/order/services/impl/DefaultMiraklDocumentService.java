package com.mirakl.hybris.core.order.services.impl;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static java.util.UUID.fromString;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.domain.common.FileWithContext;
import com.mirakl.client.mmp.domain.common.FileWrapper;
import com.mirakl.client.mmp.domain.order.document.MiraklOrderDocument;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.request.order.document.MiraklDownloadOrdersDocumentsRequest;
import com.mirakl.client.mmp.front.request.order.document.MiraklGetOrderDocumentsRequest;
import com.mirakl.client.mmp.front.request.order.message.MiraklDownloadThreadMessageAttachmentRequest;
import com.mirakl.hybris.core.order.services.MiraklDocumentService;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class DefaultMiraklDocumentService implements MiraklDocumentService {

  protected MiraklMarketplacePlatformFrontApi miraklApi;

  @Override
  public Set<MiraklOrderDocument> getDocumentsForMarketplaceConsignments(Collection<String> marketplaceConsignmentCodes) {
    if (isNotEmpty(marketplaceConsignmentCodes)) {
      MiraklGetOrderDocumentsRequest request = new MiraklGetOrderDocumentsRequest(marketplaceConsignmentCodes);
      return new HashSet<>(miraklApi.getOrderDocuments(request));
    }
    return emptySet();
  }

  @Override
  public FileWrapper downloadDocument(String documentId) {
    MiraklDownloadOrdersDocumentsRequest request = new MiraklDownloadOrdersDocumentsRequest();
    request.setDocumentIds(singletonList(documentId));
    return miraklApi.downloadOrdersDocuments(request);
  }

  @Override
  public FileWrapper downloadDocumentsForMarketplaceConsignment(String marketplaceConsignmentCode) {
    MiraklDownloadOrdersDocumentsRequest request = new MiraklDownloadOrdersDocumentsRequest();
    request.setOrderIds(singletonList(marketplaceConsignmentCode));
    return miraklApi.downloadOrdersDocuments(request);
  }

  @Override
  public String getDocumentFileName(String marketplaceConsignmentCode, String documentId) {
    Set<MiraklOrderDocument> documents = getDocumentsForMarketplaceConsignments(singletonList(marketplaceConsignmentCode));
    for (MiraklOrderDocument document : documents) {
      if (documentId.equals(document.getId())) {
        return document.getFileName();
      }
    }
    throw new UnknownIdentifierException(
        String.format("Impossible to find document [%s] for consignment [%s]", documentId, marketplaceConsignmentCode));
  }

  @Override
  public FileWithContext downloadThreadAttachment(String attachmentId) {
    return miraklApi.downloadThreadMessageAttachment(new MiraklDownloadThreadMessageAttachmentRequest(fromString(attachmentId)));
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.miraklApi = miraklApi;
  }

}
