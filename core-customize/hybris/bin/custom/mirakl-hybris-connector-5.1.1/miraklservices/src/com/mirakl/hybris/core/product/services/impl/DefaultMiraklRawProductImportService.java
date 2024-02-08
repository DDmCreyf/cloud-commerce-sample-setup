package com.mirakl.hybris.core.product.services.impl;

import static java.lang.String.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.MiraklRawProductData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.product.services.MiraklRawProductImportService;
import com.mirakl.hybris.core.util.services.CsvService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import shaded.org.supercsv.io.CsvMapReader;
import shaded.org.supercsv.io.ICsvMapReader;

public class DefaultMiraklRawProductImportService implements MiraklRawProductImportService {

  private static final Logger LOG = Logger.getLogger(DefaultMiraklRawProductImportService.class);

  protected CsvService csvService;
  protected ModelService modelService;
  protected Populator<String[], ProductImportFileContextData> headerFileContextPopulator;
  protected Converter<MiraklRawProductData, MiraklRawProductModel> miraklRawProductConverter;

  @Override
  public String importRawProducts(File inputFile, ProductImportFileContextData context) {
    String importId = UUID.randomUUID().toString();
    try (FileReader fileReader = new FileReader(inputFile);
        ICsvMapReader csvMapReader = new CsvMapReader(fileReader, csvService.getDefaultCsvPreference())) {
      final String[] header = csvMapReader.getHeader(true);
      headerFileContextPopulator.populate(header, context);

      Map<String, String> productValues;
      while ((productValues = csvMapReader.read(header)) != null) {
        MiraklRawProductData rawProductData = createRawProductData(productValues, csvMapReader, importId, context);
        modelService.save(miraklRawProductConverter.convert(rawProductData));
      }

      if (LOG.isDebugEnabled()) {
        LOG.debug(format("Parsing of file [%s] done. Import Id [%s]", inputFile, importId));
      }

    } catch (FileNotFoundException e) {
      LOG.error(format("Unable to find file [%s]", inputFile), e);
    } catch (IOException e) {
      LOG.error(format("Error on parsing file [%s]", inputFile), e);
    }

    return importId;
  }

  protected MiraklRawProductData createRawProductData(Map<String, String> productValues, ICsvMapReader csvMapReader,
      String importId, ProductImportFileContextData context) {
    MiraklRawProductData rawProductData = new MiraklRawProductData();
    rawProductData.setContext(context);
    rawProductData.setImportId(importId);
    rawProductData.setLineNumber(csvMapReader.getLineNumber());
    rawProductData.setUntokenizedRow(csvMapReader.getUntokenizedRow());
    rawProductData.setValues(productValues);
    return rawProductData;
  }

  @Required
  public void setCsvService(CsvService csvService) {
    this.csvService = csvService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setHeaderFileContextPopulator(Populator<String[], ProductImportFileContextData> headerFileContextPopulator) {
    this.headerFileContextPopulator = headerFileContextPopulator;
  }

  @Required
  public void setMiraklRawProductConverter(Converter<MiraklRawProductData, MiraklRawProductModel> miraklRawProductConverter) {
    this.miraklRawProductConverter = miraklRawProductConverter;
  }
}
