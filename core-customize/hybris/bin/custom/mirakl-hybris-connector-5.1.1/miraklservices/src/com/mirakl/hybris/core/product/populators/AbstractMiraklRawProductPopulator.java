package com.mirakl.hybris.core.product.populators;

import static com.mirakl.hybris.core.enums.MiraklAttributeRole.VARIANT_GROUP_CODE_ATTRIBUTE;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.MiraklRawProductData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.model.MiraklRawProductModel;
import com.mirakl.hybris.core.util.strategies.ChecksumCalculationStrategy;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public abstract class AbstractMiraklRawProductPopulator implements Populator<MiraklRawProductData, MiraklRawProductModel> {

  protected ChecksumCalculationStrategy checksumCalculationStrategy;

  protected abstract void populateRawProduct(MiraklRawProductData source, MiraklRawProductModel target);

  @Override
  public void populate(MiraklRawProductData source, MiraklRawProductModel target) throws ConversionException {
    String rawData = source.getUntokenizedRow();
    target.setImportId(source.getImportId());
    target.setRawData(rawData);
    target.setChecksum(checksumCalculationStrategy.calculateChecksum(rawData));
    target.setRowNumber(source.getLineNumber());
    target.setValues(source.getValues());
    String variantGroupAttributeCode = getVariantGroupAttributeCode(source.getContext());
    if (variantGroupAttributeCode != null) {
      target.setVariantGroupCode(source.getValues().get(variantGroupAttributeCode));
    }
    populateRawProduct(source, target);
  }

  protected String getVariantGroupAttributeCode(ProductImportFileContextData context) {
    return context.getGlobalContext().getCoreAttributePerRole().get(VARIANT_GROUP_CODE_ATTRIBUTE);
  }

  @Required
  public void setChecksumCalculationStrategy(ChecksumCalculationStrategy checksumCalculationStrategy) {
    this.checksumCalculationStrategy = checksumCalculationStrategy;
  }

}
