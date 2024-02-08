package com.mirakl.hybris.core.product.jobs;

import com.mirakl.hybris.core.model.MiraklImportOfferStatesCronJobModel;
import com.mirakl.hybris.core.product.services.OfferStateImportService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklImportOfferStatesJobTest {

  @InjectMocks
  private MiraklImportOfferStatesJob testObj;

  @Mock
  private OfferStateImportService offerStateImportService;

  @Mock
  private ModelService modelService;

  @Mock
  private MiraklImportOfferStatesCronJobModel miraklImportOfferStatesCronJobModel;

  @Test
  public void importsAllOfferStates() {

    PerformResult result = testObj.perform(miraklImportOfferStatesCronJobModel);

    assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
    assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);

    verify(modelService).save(miraklImportOfferStatesCronJobModel);
    verify(offerStateImportService).importAllOfferStates();
  }

}
