package com.mirakl.hybris.core.catalog.populators.impl;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mci.front.domain.hierarchy.MiraklHierarchyImportResult;
import com.mirakl.client.mci.front.request.hierarchy.MiraklHierarchyImportErrorReportRequest;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCatalogCategoryExportReportPopulatorTest
    extends AbstractMiraklCatalogExportReportPopulatorTest<MiraklHierarchyImportResult> {

  @InjectMocks
  private DefaultCatalogCategoryExportReportPopulator populator;

  @Mock
  private MiraklHierarchyImportResult importResult;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    when(mciApi.getHierarchiyImportErrorReport(new MiraklHierarchyImportErrorReportRequest(JOB_ID)))
        .thenReturn(tempFolder.newFile("report.csv"));
  }

  @Override
  protected AbstractMiraklCatalogExportReportPopulator<MiraklHierarchyImportResult> getPopulator() {
    return populator;
  }

  @Override
  protected MiraklHierarchyImportResult getImportResult() {
    return importResult;
  }

}
