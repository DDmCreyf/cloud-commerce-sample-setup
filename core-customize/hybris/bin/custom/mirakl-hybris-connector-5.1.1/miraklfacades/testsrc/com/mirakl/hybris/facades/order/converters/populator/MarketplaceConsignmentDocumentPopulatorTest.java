package com.mirakl.hybris.facades.order.converters.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.document.MiraklOrderDocument;
import com.mirakl.hybris.beans.DocumentData;

import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MarketplaceConsignmentDocumentPopulatorTest {

  private static final String DOCUMENT_ID = "1025";
  private static final String DOCUMENT_FILE_NAME = "file_name.pdf";
  private static final String DOCUMENT_FILE_TYPE = "application/PDF";
  private static final Date DOCUMENT_DATE_UPLOADED = new Date(1000);

  @Mock
  private MiraklOrderDocument miraklOrderDocument;

  @InjectMocks
  private MarketplaceConsignmentDocumentPopulator testObj;

  @Test
  public void populate() throws Exception {
    when(miraklOrderDocument.getId()).thenReturn(DOCUMENT_ID);
    when(miraklOrderDocument.getDateUploaded()).thenReturn(DOCUMENT_DATE_UPLOADED);
    when(miraklOrderDocument.getFileName()).thenReturn(DOCUMENT_FILE_NAME);
    when(miraklOrderDocument.getTypeCode()).thenReturn(DOCUMENT_FILE_TYPE);
    DocumentData output = new DocumentData();

    testObj.populate(miraklOrderDocument, output);

    assertThat(output.getCode()).isEqualTo(DOCUMENT_ID);
    assertThat(output.getDateUploaded()).isEqualTo(DOCUMENT_DATE_UPLOADED);
    assertThat(output.getFileName()).isEqualTo(DOCUMENT_FILE_NAME);
    assertThat(output.getType()).isEqualTo(DOCUMENT_FILE_TYPE);
  }

}
