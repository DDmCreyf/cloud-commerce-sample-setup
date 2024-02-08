package com.mirakl.hybris.core.catalog.populators.impl;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mci.front.domain.value.list.MiraklValueListImportResult;
import com.mirakl.client.mci.front.request.value.list.MiraklValueListImportErrorReportRequest;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultValueListExportReportPopulatorTest
    extends AbstractMiraklCatalogExportReportPopulatorTest<MiraklValueListImportResult> {

  @InjectMocks
  private DefaultValueListExportReportPopulator populator;

  @Mock
  private MiraklValueListImportResult importResult;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    when(mciApi.getValueListImportErrorReport(new MiraklValueListImportErrorReportRequest(JOB_ID)))
        .thenReturn(tempFolder.newFile());
  }

  @Override
  protected AbstractMiraklCatalogExportReportPopulator<MiraklValueListImportResult> getPopulator() {
    return populator;
  }

  @Override
  protected MiraklValueListImportResult getImportResult() {
    return importResult;
  }


}
