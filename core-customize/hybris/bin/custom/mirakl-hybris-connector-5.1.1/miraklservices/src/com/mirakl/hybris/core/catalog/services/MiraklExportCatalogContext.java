package com.mirakl.hybris.core.catalog.services;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.mirakl.hybris.beans.ExportCatalogAdditionalData;
import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.writer.ExportCatalogWriter;

import de.hybris.platform.category.model.CategoryModel;

public class MiraklExportCatalogContext {

  protected MiraklExportCatalogConfig exportConfig;
  protected CategoryModel currentParentCategory;
  protected Set<String> visitedClassIds;
  protected Set<String> exportedValueListCodes;
  protected Set<String> miraklCategoryCodes;
  protected Set<Pair<String, String>> miraklAttributeCodes;
  protected Set<Pair<String, String>> miraklValueCodes;
  protected ExportCatalogWriter writer;
  protected ExportCatalogAdditionalData additionalData;

  public MiraklExportCatalogConfig getExportConfig() {
    return exportConfig;
  }

  public void setExportConfig(MiraklExportCatalogConfig exportConfig) {
    this.exportConfig = exportConfig;
  }

  public CategoryModel getCurrentParentCategory() {
    return currentParentCategory;
  }

  public void setCurrentParentCategory(CategoryModel currentParentCategory) {
    this.currentParentCategory = currentParentCategory;
  }

  public Set<String> getVisitedClassIds() {
    return visitedClassIds;
  }

  public void setVisitedClassIds(Set<String> visitedClassIds) {
    this.visitedClassIds = visitedClassIds;
  }

  public Set<String> getMiraklCategoryCodes() {
    return miraklCategoryCodes;
  }

  public void setMiraklCategoryCodes(Set<String> miraklCategoryCodes) {
    this.miraklCategoryCodes = miraklCategoryCodes;
  }

  public Set<Pair<String, String>> getMiraklAttributeCodes() {
    return miraklAttributeCodes;
  }

  public void setMiraklAttributeCodes(Set<Pair<String, String>> miraklAttributeCodes) {
    this.miraklAttributeCodes = miraklAttributeCodes;
  }

  public Set<Pair<String, String>> getMiraklValueCodes() {
    return miraklValueCodes;
  }

  public void setMiraklValueCodes(Set<Pair<String, String>> miraklValueCodes) {
    this.miraklValueCodes = miraklValueCodes;
  }

  public ExportCatalogWriter getWriter() {
    return writer;
  }

  public void setWriter(ExportCatalogWriter writer) {
    this.writer = writer;
  }

  public ExportCatalogAdditionalData getAdditionalData() {
    return additionalData;
  }

  public void setAdditionalData(ExportCatalogAdditionalData additionalData) {
    this.additionalData = additionalData;
  }

  public Set<String> getExportedValueListCodes() {
    return exportedValueListCodes;
  }

  public void setExportedValueListCodes(Set<String> exportedValueListCodes) {
    this.exportedValueListCodes = exportedValueListCodes;
  }

  public boolean removeMiraklCategoryCode(String code) {
    return miraklCategoryCodes.remove(code);
  }

  public boolean removeMiraklAttributeCode(Pair<String, String> attribute) {
    return miraklAttributeCodes.remove(attribute);
  }

  public boolean removeMiraklValueCode(Pair<String, String> value) {
    return miraklValueCodes.remove(value);
  }
}
