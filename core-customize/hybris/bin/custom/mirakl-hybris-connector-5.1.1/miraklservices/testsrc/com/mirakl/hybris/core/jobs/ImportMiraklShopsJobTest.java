package com.mirakl.hybris.core.jobs;

import com.mirakl.hybris.core.model.MiraklImportShopsCronjobModel;
import com.mirakl.hybris.core.shop.jobs.MiraklImportShopsJob;
import com.mirakl.hybris.core.shop.services.ShopImportService;

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

import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com
 * All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ImportMiraklShopsJobTest {

    @InjectMocks
    private MiraklImportShopsJob miraklImportShopsJob = new MiraklImportShopsJob();

    @Mock
    private MiraklImportShopsCronjobModel cronjobModel;

    @Mock
    private ModelService modelService;

    @Mock
    private ShopImportService shopImportService;

    @Test
    public void shouldPerformAFullImportWhenNoDateIsSpecified(){
        when(cronjobModel.getLastExecutionDate()).thenReturn(null);

        PerformResult result = miraklImportShopsJob.perform(cronjobModel);

        assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
        assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);
        verify(shopImportService).importAllShops();
    }

    @Test
    public void shouldPerformAPartialImportWhenDateAndTypeAreSpecified(){
        Date date = new Date();
        when(cronjobModel.getLastExecutionDate()).thenReturn(date);
        when(cronjobModel.isFullImport()).thenReturn(Boolean.FALSE);

        PerformResult result = miraklImportShopsJob.perform(cronjobModel);

        assertThat(result.getResult()).isEqualTo(CronJobResult.SUCCESS);
        assertThat(result.getStatus()).isEqualTo(CronJobStatus.FINISHED);
        verify(shopImportService).importShopsUpdatedSince(date);
    }

    @Test
    public void shouldPerformAFullImportWhenTypeIsSpecified(){
        when(cronjobModel.isFullImport()).thenReturn(Boolean.TRUE);

        miraklImportShopsJob.perform(cronjobModel);

        verify(shopImportService).importAllShops();
    }




}
