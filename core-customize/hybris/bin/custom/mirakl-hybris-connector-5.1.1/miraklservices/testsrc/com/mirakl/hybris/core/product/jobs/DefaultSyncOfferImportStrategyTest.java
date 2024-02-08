package com.mirakl.hybris.core.product.jobs;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.hybris.core.model.MiraklImportOffersCronJobModel;
import com.mirakl.hybris.core.product.services.OfferImportService;
import com.mirakl.hybris.core.product.strategies.impl.DefaultSyncOfferImportStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSyncOfferImportStrategyTest {

  @InjectMocks
  private DefaultSyncOfferImportStrategy testObj = new DefaultSyncOfferImportStrategy();

  @Mock
  private OfferImportService offerImportServiceMock;

  @Mock
  private ModelService modelServiceMock;

  @Mock
  private MiraklImportOffersCronJobModel miraklImportOffersCronJobMock;

  @Mock
  private Date lastImportDateMock, startDateMock;

  @Before
  public void setUp() {
    when(miraklImportOffersCronJobMock.getStartTime()).thenReturn(startDateMock);
  }

  @Test
  public void importsUpdatedOffers() {
    when(miraklImportOffersCronJobMock.getLastImportTime()).thenReturn(lastImportDateMock);
    when(miraklImportOffersCronJobMock.isFullImport()).thenReturn(false);

    PerformResult result = testObj.perform(miraklImportOffersCronJobMock);

    assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);

    verify(miraklImportOffersCronJobMock).setLastImportTime(startDateMock);
    verify(modelServiceMock).save(miraklImportOffersCronJobMock);
    verify(offerImportServiceMock).importOffersUpdatedSince(lastImportDateMock);
  }

  @Test
  public void importsAllOffers() {
    when(miraklImportOffersCronJobMock.getLastImportTime()).thenReturn(null);
    when(miraklImportOffersCronJobMock.isFullImport()).thenReturn(false);
    when(miraklImportOffersCronJobMock.isIncludeInactiveOffers()).thenReturn(true);

    PerformResult result = testObj.perform(miraklImportOffersCronJobMock);

    assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);

    verify(miraklImportOffersCronJobMock).setLastImportTime(startDateMock);
    verify(modelServiceMock).save(miraklImportOffersCronJobMock);
    verify(offerImportServiceMock).importAllOffers(startDateMock, true);
  }

  @Test
  public void importOffersEndsWithErrorIfMiraklAPIExceptionIsThrownAndLastImportDateIsNotUpdated() {
    when(miraklImportOffersCronJobMock.getLastImportTime()).thenReturn(lastImportDateMock);
    doThrow(MiraklApiException.class).when(offerImportServiceMock).importOffersUpdatedSince(lastImportDateMock);

    PerformResult result = testObj.perform(miraklImportOffersCronJobMock);

    assertThat(result.getResult()).isEqualTo(CronJobResult.ERROR);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.ABORTED);

    verify(miraklImportOffersCronJobMock, never()).setLastImportTime(any(Date.class));
    verify(modelServiceMock, never()).save(any(MiraklImportOffersCronJobModel.class));
    verify(offerImportServiceMock).importOffersUpdatedSince(lastImportDateMock);
  }
}
