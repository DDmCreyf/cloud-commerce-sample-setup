package com.mirakl.hybris.core.catalog.populators.impl;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mci.front.domain.attribute.MiraklAttributeImportResult;
import com.mirakl.client.mci.front.request.attribute.MiraklAttributeImportErrorReportRequest;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAttributeExportReportPopulatorTest
    extends AbstractMiraklCatalogExportReportPopulatorTest<MiraklAttributeImportResult> {

  @InjectMocks
  private DefaultAttributeExportReportPopulator populator;
  @Mock
  private MiraklAttributeImportResult importResult;


  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    when(mciApi.getAttributeImportErrorReport(new MiraklAttributeImportErrorReportRequest(JOB_ID)))
        .thenReturn(tempFolder.newFile());
  }

  @Override
  protected AbstractMiraklCatalogExportReportPopulator<MiraklAttributeImportResult> getPopulator() {
    return populator;
  }

  @Override
  protected MiraklAttributeImportResult getImportResult() {
    return importResult;
  }
}
