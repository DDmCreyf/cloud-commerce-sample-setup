package com.mirakl.hybris.core.catalog.attributes.utils;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mirakl.hybris.core.enums.MiraklAttributeType;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

public class TypeParametersUtils {

  protected static Pattern dateTypeParametersPattern = Pattern.compile("PATTERN\\|([^,]*)");
  protected static Pattern listTypeParametersPattern = Pattern.compile("LIST_CODE\\|([^,]*)");

  public static String extractDatePatternFromTypeParameters(MiraklCoreAttributeModel attribute) {
    if (MiraklAttributeType.DATE != attribute.getType()) {
      return null;
    }
    return extractPattern(attribute, dateTypeParametersPattern);
  }

  public static String extractListCodeFromTypeParameters(MiraklCoreAttributeModel attribute) {
    if (MiraklAttributeType.LIST != attribute.getType() && MiraklAttributeType.LIST_MULTIPLE_VALUES != attribute.getType()) {
      return null;
    }
    return extractPattern(attribute, listTypeParametersPattern);
  }

  protected static String extractPattern(MiraklCoreAttributeModel attribute, Pattern pattern) {
    String typeParameters = attribute.getTypeParameters();
    if (isBlank(typeParameters)) {
      return null;
    }
    Matcher matcher = pattern.matcher(attribute.getTypeParameters());
    return matcher.find() ? matcher.group(1) : null;
  }

}
