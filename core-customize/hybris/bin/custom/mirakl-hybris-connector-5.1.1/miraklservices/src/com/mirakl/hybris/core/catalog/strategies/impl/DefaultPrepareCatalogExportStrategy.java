package com.mirakl.hybris.core.catalog.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.BOOLEAN_VALUE_LIST_ID;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.strategies.PrepareCatalogExportStrategy;
import com.mirakl.hybris.core.enums.MiraklValueListExportHeader;

public class DefaultPrepareCatalogExportStrategy implements PrepareCatalogExportStrategy {

  @Override
  public void prepareExport(MiraklExportCatalogContext context) throws IOException {
    if (!removeBooleanListFromMiraklValueLists(context)) {
      createValueListForBooleanType(context);
    }
  }

  protected boolean removeBooleanListFromMiraklValueLists(MiraklExportCatalogContext context) {
    return context.removeMiraklValueCode(Pair.of(Boolean.FALSE.toString(), BOOLEAN_VALUE_LIST_ID))
        && context.removeMiraklValueCode(Pair.of(Boolean.TRUE.toString(), BOOLEAN_VALUE_LIST_ID));
  }

  protected void createValueListForBooleanType(MiraklExportCatalogContext context) throws IOException {
    if (context.getExportConfig().isExportValueLists()) {
      context.getWriter().writeAttributeValue(buildBooleanValueListLine(Boolean.FALSE));
      context.getWriter().writeAttributeValue(buildBooleanValueListLine(Boolean.TRUE));
    }
  }

  protected Map<String, String> buildBooleanValueListLine(Boolean value) {
    Map<String, String> line = new HashMap<>();
    line.put(MiraklValueListExportHeader.LIST_CODE.getCode(), BOOLEAN_VALUE_LIST_ID);
    line.put(MiraklValueListExportHeader.LIST_LABEL.getCode(), BOOLEAN_VALUE_LIST_ID);
    line.put(MiraklValueListExportHeader.VALUE_CODE.getCode(), Boolean.toString(value));
    line.put(MiraklValueListExportHeader.VALUE_LABEL.getCode(), Boolean.toString(value));

    return line;
  }

}
