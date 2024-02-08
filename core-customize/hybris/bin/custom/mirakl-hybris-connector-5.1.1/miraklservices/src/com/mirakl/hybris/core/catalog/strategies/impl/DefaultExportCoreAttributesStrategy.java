package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.BOOLEAN_TYPE_PARAMETERS;
import static java.lang.String.format;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.MiraklExportCatalogConfig;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogService;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandler;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandlerResolver;
import com.mirakl.hybris.core.catalog.strategies.ExportCoreAttributesStrategy;
import com.mirakl.hybris.core.enums.MiraklAttributeExportHeader;
import com.mirakl.hybris.core.enums.MiraklAttributeType;
import com.mirakl.hybris.core.enums.MiraklValueListExportHeader;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.servicelayer.config.ConfigurationService;

public class DefaultExportCoreAttributesStrategy implements ExportCoreAttributesStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultExportCoreAttributesStrategy.class);

  protected CoreAttributeHandlerResolver coreAttributeHandlerResolver;
  protected ConfigurationService configurationService;
  protected MiraklExportCatalogService exportCatalogService;

  @Override
  public void exportCoreAttributes(MiraklExportCatalogContext context) {
    Set<MiraklCoreAttributeModel> coreAttributes = context.getExportConfig().getCoreAttributes();
    LOG.info(format("Started processing Core Attributes.. (found [%s])", coreAttributes.size()));

    for (MiraklCoreAttributeModel attribute : coreAttributes) {
      handleCoreAttributeExport(attribute, context);
      exportValues(attribute, context);
    }
  }

  protected void handleCoreAttributeExport(MiraklCoreAttributeModel attribute, MiraklExportCatalogContext context) {
    if (!context.getExportConfig().isExportAttributes()) {
      return;
    }
    try {
      if (attribute.isLocalized()) {
        for (Locale translatableLocale : context.getExportConfig().getTranslatableLocales()) {
          exportCoreAttribute(attribute, context, translatableLocale);
        }
      } else {
        exportCoreAttribute(attribute, context);
      }
    } catch (Exception e) {
      LOG.error(format("Unable to export core attribute [%s]", attribute.getCode()), e);
    }
  }

  protected void exportCoreAttribute(MiraklCoreAttributeModel attribute, MiraklExportCatalogContext context) throws IOException {
    exportCoreAttribute(attribute, context, null);
  }

  protected void exportCoreAttribute(MiraklCoreAttributeModel attribute, MiraklExportCatalogContext context,
      Locale translatableLocale) throws IOException {
    context.getWriter().writeAttribute(buildAttributeLine(context, translatableLocale, attribute));
    context.removeMiraklAttributeCode(
        Pair.of(exportCatalogService.formatAttributeExportName(attribute.getCode(), translatableLocale),
            attribute.getCategory() != null ? attribute.getCategory().getCode() : ""));
  }

  protected Map<String, String> buildAttributeLine(MiraklExportCatalogContext context, Locale translatableLocale,
      MiraklCoreAttributeModel attribute) {
    MiraklExportCatalogConfig exportConfig = context.getExportConfig();

    Map<String, String> line = new HashMap<>();
    line.put(MiraklAttributeExportHeader.CODE.getCode(),
        exportCatalogService.formatAttributeExportName(attribute.getCode(), translatableLocale));
    line.put(MiraklAttributeExportHeader.LABEL.getCode(),
        exportCatalogService.formatAttributeExportName(attribute.getLabel(exportConfig.getDefaultLocale()), translatableLocale));
    line.put(MiraklAttributeExportHeader.DESCRIPTION.getCode(), attribute.getDescription(exportConfig.getDefaultLocale()));
    line.put(MiraklAttributeExportHeader.REQUIREMENT_LEVEL.getCode(), attribute.getRequirementLevel().getCode());
    line.put(MiraklAttributeExportHeader.VARIANT.getCode(), Boolean.toString(attribute.isVariant()));
    line.put(MiraklAttributeExportHeader.DEFAULT_VALUE.getCode(), attribute.getDefaultValue());

    if (attribute.getCategory() != null) {
      line.put(MiraklAttributeExportHeader.HIERARCHY_CODE.getCode(), attribute.getCategory().getCode());
    }

    if (attribute.getType() == MiraklAttributeType.BOOLEAN) {
      line.put(MiraklAttributeExportHeader.TYPE.getCode(), MiraklAttributeType.LIST.getCode());
      line.put(MiraklAttributeExportHeader.TYPE_PARAMETERS.getCode(), BOOLEAN_TYPE_PARAMETERS);
    } else {
      line.put(MiraklAttributeExportHeader.TYPE.getCode(), attribute.getType().getCode());
      line.put(MiraklAttributeExportHeader.TYPE_PARAMETERS.getCode(), attribute.getTypeParameters());
    }

    for (Locale additionalLocale : exportConfig.getAdditionalLocales()) {
      line.put(MiraklAttributeExportHeader.LABEL.getCode(additionalLocale),
          exportCatalogService.formatAttributeExportName(attribute.getLabel(additionalLocale), translatableLocale));
      line.put(MiraklAttributeExportHeader.DESCRIPTION.getCode(additionalLocale), attribute.getDescription(additionalLocale));
    }

    return line;
  }

  protected void exportValues(MiraklCoreAttributeModel attribute, MiraklExportCatalogContext context) {
    if (!context.getExportConfig().isExportValueLists()
        || !EnumSet.of(MiraklAttributeType.LIST, MiraklAttributeType.LIST_MULTIPLE_VALUES, MiraklAttributeType.MULTIPLE)
            .contains(attribute.getType())) {
      return;
    }

    CoreAttributeHandler<MiraklCoreAttributeModel> valueHandler =
        coreAttributeHandlerResolver.determineHandler(attribute, context);
    for (Map<String, String> line : valueHandler.getValues(attribute, context)) {
      writeValueListLine(context, line);
      context.removeMiraklValueCode(Pair.of(line.get(MiraklValueListExportHeader.VALUE_CODE.getCode()),
          line.get(MiraklValueListExportHeader.LIST_CODE.getCode())));
    }
  }

  protected void writeValueListLine(MiraklExportCatalogContext context, Map<String, String> line) {
    try {
      context.getWriter().writeAttributeValue(line);
    } catch (Exception e) {
      LOG.error(format("Unable to export core attribute value [%s]", line), e);
    }
  }

  @Required
  public void setCoreAttributeHandlerResolver(CoreAttributeHandlerResolver coreAttributeHandlerResolver) {
    this.coreAttributeHandlerResolver = coreAttributeHandlerResolver;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setExportCatalogService(MiraklExportCatalogService exportCatalogService) {
    this.exportCatalogService = exportCatalogService;
  }
}
