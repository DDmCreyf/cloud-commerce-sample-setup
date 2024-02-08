package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.events.ExportableAttributeEvent;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogService;
import com.mirakl.hybris.core.catalog.strategies.AttributeDefaultValueStrategy;
import com.mirakl.hybris.core.catalog.strategies.AttributeVarianceStrategy;
import com.mirakl.hybris.core.catalog.strategies.ClassificationAttributeExportEligibilityStrategy;
import com.mirakl.hybris.core.catalog.strategies.ValueListNamingStrategy;
import com.mirakl.hybris.core.catalog.strategies.WriteAttributeStrategy;
import com.mirakl.hybris.core.enums.MiraklAttributeExportHeader;
import com.mirakl.hybris.core.enums.MiraklAttributeType;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

public class DefaultWriteAttributeStrategy implements WriteAttributeStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultWriteAttributeStrategy.class);

  protected ConfigurationService configurationService;
  protected MiraklExportCatalogService exportCatalogService;
  protected ValueListNamingStrategy valueListNamingStrategy;
  protected AttributeVarianceStrategy attributeVarianceStrategy;
  protected AttributeDefaultValueStrategy attributeDefaultValueStrategy;
  protected ClassificationAttributeExportEligibilityStrategy attributeExportEligibilityStrategy;

  @Override
  public void handleEvent(ExportableAttributeEvent event) {
    MiraklExportCatalogContext context = event.getContext();
    ClassAttributeAssignmentModel assignment = event.getAttributeAssignment();
    CategoryModel currentCategory = event.getCurrentCategory();

    if (context.getExportConfig().isExportAttributes() && attributeExportEligibilityStrategy.isExportableAttribute(assignment)) {
      try {
        writeAttribute(assignment, currentCategory, context);
      } catch (IOException e) {
        LOG.error(format("Unable to export attribute [%s]", assignment.getClassificationAttribute().getCode()), e);
      }
    }
  }

  protected void writeAttribute(ClassAttributeAssignmentModel assignment, CategoryModel currentCategory,
      MiraklExportCatalogContext context) throws IOException {
    if (shouldBeLocalized(assignment, context)) {
      for (Locale locale : context.getExportConfig().getTranslatableLocales()) {
        writeLine(assignment, currentCategory, locale, context);
      }
      return;
    }

    writeLine(assignment, currentCategory, null, context);
  }

  protected boolean shouldBeLocalized(ClassAttributeAssignmentModel assignment, MiraklExportCatalogContext context) {
    return isTrue(assignment.getLocalized()) && isNotEmpty(context.getExportConfig().getTranslatableLocales());
  }

  protected void writeLine(ClassAttributeAssignmentModel assignment, CategoryModel currentCategory, Locale locale,
      MiraklExportCatalogContext context) throws IOException {
    context.getWriter().writeAttribute(buildLine(assignment, currentCategory, context, locale));
    context.removeMiraklAttributeCode(
        Pair.of(exportCatalogService.formatAttributeExportName(assignment.getClassificationAttribute().getCode(), locale),
            exportCatalogService.getCategoryExportCode(currentCategory, context)));
  }

  protected Map<String, String> buildLine(ClassAttributeAssignmentModel source, CategoryModel currentCategory,
      MiraklExportCatalogContext context, Locale locale) {
    ClassificationAttributeModel attribute = source.getClassificationAttribute();
    Locale defaultLocale = context.getExportConfig().getDefaultLocale();

    Map<String, String> line = new HashMap<>();
    line.put(MiraklAttributeExportHeader.CODE.getCode(),
        exportCatalogService.formatAttributeExportName(attribute.getCode(), locale));
    line.put(MiraklAttributeExportHeader.REQUIREMENT_LEVEL.getCode(), source.getMarketplaceRequirementLevel().getCode());
    line.put(MiraklAttributeExportHeader.VARIANT.getCode(), Boolean.toString(attributeVarianceStrategy.isVariant(source)));
    line.put(MiraklAttributeExportHeader.DEFAULT_VALUE.getCode(), attributeDefaultValueStrategy.<String>getDefaultValue(source));
    line.put(MiraklAttributeExportHeader.LABEL.getCode(),
        exportCatalogService.formatAttributeExportName(attribute.getName(defaultLocale), locale));
    line.put(MiraklAttributeExportHeader.DESCRIPTION.getCode(), source.getDescription(defaultLocale));

    for (Locale additionalLocale : context.getExportConfig().getAdditionalLocales()) {
      line.put(MiraklAttributeExportHeader.LABEL.getCode(additionalLocale),
          exportCatalogService.formatAttributeExportName(attribute.getName(additionalLocale), locale));
      line.put(MiraklAttributeExportHeader.DESCRIPTION.getCode(additionalLocale), source.getDescription(additionalLocale));
    }
    line.put(MiraklAttributeExportHeader.HIERARCHY_CODE.getCode(),
        exportCatalogService.getCategoryExportCode(currentCategory, context));
    line.put(MiraklAttributeExportHeader.TYPE.getCode(), getAttributeType(source));
    line.put(MiraklAttributeExportHeader.TYPE_PARAMETERS.getCode(), source.getMarketplaceTypeParameters());

    return line;
  }

  protected String getAttributeType(ClassAttributeAssignmentModel source) {
    switch (source.getAttributeType()) {
      case BOOLEAN:
        return MiraklAttributeType.LIST.getCode();
      case DATE:
        return MiraklAttributeType.DATE.getCode();
      case NUMBER:
        return MiraklAttributeType.DECIMAL.getCode();
      case ENUM:
        return isTrue(source.getMultiValued()) ? MiraklAttributeType.LIST_MULTIPLE_VALUES.getCode() : MiraklAttributeType.LIST.getCode();
      default:
        return MiraklAttributeType.TEXT.getCode();
    }
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setValueListNamingStrategy(ValueListNamingStrategy valueListNamingStrategy) {
    this.valueListNamingStrategy = valueListNamingStrategy;
  }

  @Required
  public void setAttributeVarianceStrategy(AttributeVarianceStrategy attributeVarianceStrategy) {
    this.attributeVarianceStrategy = attributeVarianceStrategy;
  }

  @Required
  public void setAttributeDefaultValueStrategy(AttributeDefaultValueStrategy attributeDefaultValueStrategy) {
    this.attributeDefaultValueStrategy = attributeDefaultValueStrategy;
  }

  @Required
  public void setExportCatalogService(MiraklExportCatalogService exportCatalogService) {
    this.exportCatalogService = exportCatalogService;
  }

  @Required
  public void setAttributeExportEligibilityStrategy(
      ClassificationAttributeExportEligibilityStrategy attributeExportEligibilityStrategy) {
    this.attributeExportEligibilityStrategy = attributeExportEligibilityStrategy;
  }
}
