package com.mirakl.hybris.core.jobs.populators;

import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.domain.common.MiraklProcessTrackingStatus;
import com.mirakl.client.mci.front.core.MiraklCatalogIntegrationFrontApi;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.hybris.core.enums.MiraklExportStatus;
import com.mirakl.hybris.core.enums.MiraklExportType;
import com.mirakl.hybris.core.model.MiraklJobReportModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

@Ignore
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractMiraklJobReportPopulatorTest {

  protected static final String JOB_ID = "job-id";

  @Mock
  protected ModelService modelService;
  @Mock
  protected MediaService mediaService;
  @Mock
  protected MiraklCatalogIntegrationFrontApi mciApi;
  @Mock
  protected MiraklMarketplacePlatformFrontApi mmpApi;
  @Mock
  protected CatalogUnawareMediaModel media;
  @Mock
  private Map<MiraklProcessTrackingStatus, MiraklExportStatus> exportStatuses;
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  protected MiraklJobReportModel target;

  @Before
  public void setUp() throws Exception {
    target = new MiraklJobReportModel();
    target.setJobId(JOB_ID);
    target.setReportType(MiraklExportType.CATALOG_CATEGORY_EXPORT);
    when(modelService.create(CatalogUnawareMediaModel.class)).thenReturn(media);
    when(exportStatuses.get(MiraklProcessTrackingStatus.COMPLETE)).thenReturn(MiraklExportStatus.COMPLETE);
  }

}
