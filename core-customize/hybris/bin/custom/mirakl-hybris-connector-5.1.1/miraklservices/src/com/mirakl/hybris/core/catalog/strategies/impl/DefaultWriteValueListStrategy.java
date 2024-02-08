package com.mirakl.hybris.core.catalog.strategies.impl;

import static java.lang.String.format;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.catalog.events.ExportableAttributeEvent;
import com.mirakl.hybris.core.catalog.events.listeners.WriteValueListEventListener;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.strategies.ValueListNamingStrategy;
import com.mirakl.hybris.core.catalog.strategies.WriteValueListStrategy;
import com.mirakl.hybris.core.enums.MiraklValueListExportHeader;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;

public class DefaultWriteValueListStrategy implements WriteValueListStrategy {

  private static final Logger LOG = Logger.getLogger(WriteValueListEventListener.class);

  protected ValueListNamingStrategy namingStrategy;

  @Override
  public void handleEvent(ExportableAttributeEvent event) {
    MiraklExportCatalogContext context = event.getContext();
    ClassAttributeAssignmentModel classAttributeAssignment = event.getAttributeAssignment();

    if (context.getExportConfig().isExportValueLists()
        && ClassificationAttributeTypeEnum.ENUM == classAttributeAssignment.getAttributeType()) {

      String valueListCode = namingStrategy.getCode(classAttributeAssignment);
      if (context.getExportedValueListCodes().add(valueListCode)) {
        writeAssignmentValueList(classAttributeAssignment, valueListCode, context);
      }
    }
  }

  protected void writeAssignmentValueList(ClassAttributeAssignmentModel classAttributeAssignment, String valueListCode,
      MiraklExportCatalogContext context) {
    for (ClassificationAttributeValueModel attributeValue : classAttributeAssignment.getAttributeValues()) {
      try {
        context.getWriter().writeAttributeValue(buildLine(attributeValue, classAttributeAssignment, context));
        context.removeMiraklValueCode(Pair.of(attributeValue.getCode(), valueListCode));
      } catch (IOException e) {
        LOG.error(format("Unable to export attribute value [%s]", attributeValue.getCode()), e);
      }
    }
  }

  protected Map<String, String> buildLine(ClassificationAttributeValueModel source,
      ClassAttributeAssignmentModel classAttributeAssignment, MiraklExportCatalogContext context) {
    Locale defaultLocale = context.getExportConfig().getDefaultLocale();

    Map<String, String> target = new HashMap<>();
    target.put(MiraklValueListExportHeader.LIST_CODE.getCode(), namingStrategy.getCode(classAttributeAssignment));
    target.put(MiraklValueListExportHeader.VALUE_CODE.getCode(), source.getCode());
    target.put(MiraklValueListExportHeader.VALUE_LABEL.getCode(), source.getName(defaultLocale));
    target.put(MiraklValueListExportHeader.LIST_LABEL.getCode(),
        namingStrategy.getLabel(classAttributeAssignment, defaultLocale));

    for (Locale additionalLocale : context.getExportConfig().getAdditionalLocales()) {
      target.put(MiraklValueListExportHeader.VALUE_LABEL.getCode(additionalLocale), source.getName(additionalLocale));
      target.put(MiraklValueListExportHeader.LIST_LABEL.getCode(additionalLocale),
          namingStrategy.getLabel(classAttributeAssignment, additionalLocale));
    }

    return target;
  }

  @Required
  public void setNamingStrategy(ValueListNamingStrategy namingStrategy) {
    this.namingStrategy = namingStrategy;
  }
}
