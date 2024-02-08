package com.mirakl.hybris.core.order.services.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.FileWrapper;
import com.mirakl.client.mmp.domain.order.document.MiraklOrderDocument;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.request.order.document.MiraklDownloadOrdersDocumentsRequest;
import com.mirakl.client.mmp.front.request.order.document.MiraklGetOrderDocumentsRequest;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklDocumentServiceTest {

  private static final String CONSIGNMENT_CODE_1 = "ce003377-4117-4517-a28e-3ee1bc7bf684-A";
  private static final String CONSIGNMENT_CODE_2 = "ce003377-4117-4517-a28e-3ee1bc7bf684-B";
  private static final String CONSIGNMENT_CODE_3 = "ce003377-4117-4517-a28e-3ee1bc7bf684-C";
  private static final String DOCUMENT_ID_1 = "4510";
  private static final String DOCUMENT_ID_2 = "4511";
  private static final String DOCUMENT_ID_3 = "4512";
  private static final String DOCUMENT_FILE_NAME_2 = "invoice_corrected.pdf";

  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;

  @Mock
  private MiraklOrderDocument document1, document2, document3;

  @Mock
  private FileWrapper downloadedDocument;

  @InjectMocks
  private DefaultMiraklDocumentService testObj;

  @Before
  public void setUp() throws Exception {
    when(miraklApi.downloadOrdersDocuments(any(MiraklDownloadOrdersDocumentsRequest.class))).thenReturn(downloadedDocument);
    when(miraklApi.getOrderDocuments(any(MiraklGetOrderDocumentsRequest.class))).thenReturn(asList(document1, document2));
    when(document2.getId()).thenReturn(DOCUMENT_ID_2);
    when(document3.getId()).thenReturn(DOCUMENT_ID_3);
    when(document2.getFileName()).thenReturn(DOCUMENT_FILE_NAME_2);
  }

  @Test
  public void getDocumentsForMarketplaceConsignments() throws Exception {
    ArgumentCaptor<MiraklGetOrderDocumentsRequest> requestCaptor = ArgumentCaptor.forClass(MiraklGetOrderDocumentsRequest.class);

    testObj.getDocumentsForMarketplaceConsignments(asList(CONSIGNMENT_CODE_1, CONSIGNMENT_CODE_2, CONSIGNMENT_CODE_3));

    verify(miraklApi).getOrderDocuments(requestCaptor.capture());
    assertThat(requestCaptor.getValue().getOrderIds()).containsOnly(CONSIGNMENT_CODE_1, CONSIGNMENT_CODE_2, CONSIGNMENT_CODE_3);
  }

  @Test
  public void getDocumentsForMarketplaceConsignmentsWhenEmpty() throws Exception {
    Set<MiraklOrderDocument> output = testObj.getDocumentsForMarketplaceConsignments(Collections.<String>emptyList());

    assertThat(output).isEmpty();
  }

  @Test
  public void downloadDocument() throws Exception {
    ArgumentCaptor<MiraklDownloadOrdersDocumentsRequest> requestCaptor =
        ArgumentCaptor.forClass(MiraklDownloadOrdersDocumentsRequest.class);

    FileWrapper output = testObj.downloadDocument(DOCUMENT_ID_1);

    verify(miraklApi).downloadOrdersDocuments(requestCaptor.capture());
    assertThat(requestCaptor.getValue().getDocumentIds()).containsOnly(DOCUMENT_ID_1);
    assertThat(output).isEqualTo(downloadedDocument);
  }

  @Test
  public void downloadDocumentsForMarketplaceConsignment() throws Exception {
    ArgumentCaptor<MiraklDownloadOrdersDocumentsRequest> requestCaptor =
        ArgumentCaptor.forClass(MiraklDownloadOrdersDocumentsRequest.class);

    FileWrapper output = testObj.downloadDocumentsForMarketplaceConsignment(CONSIGNMENT_CODE_1);

    verify(miraklApi).downloadOrdersDocuments(requestCaptor.capture());
    assertThat(requestCaptor.getValue().getOrderIds()).containsOnly(CONSIGNMENT_CODE_1);
    assertThat(output).isEqualTo(downloadedDocument);
  }

  @Test
  public void getDocumentFileName() throws Exception {
    String output = testObj.getDocumentFileName(CONSIGNMENT_CODE_1, DOCUMENT_ID_2);

    assertThat(output).isEqualTo(DOCUMENT_FILE_NAME_2);
  }

  @Test(expected = UnknownIdentifierException.class)
  public void getDocumentFileNameWhenWrongDocument() throws Exception {
    when(miraklApi.getOrderDocuments(any(MiraklGetOrderDocumentsRequest.class))).thenReturn(asList(document3));
    testObj.getDocumentFileName(CONSIGNMENT_CODE_2, DOCUMENT_ID_1);
  }

}
