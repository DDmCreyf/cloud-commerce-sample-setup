package com.mirakl.hybris.core.product.populators;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections.MapUtils.isEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mci.domain.product.MiraklProductDataSheetSyncItem;
import com.mirakl.hybris.beans.ProductDataSheetExportContextData;
import com.mirakl.hybris.core.catalog.attributes.McmCoreAttributeHandler;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogService;
import com.mirakl.hybris.core.catalog.strategies.ClassificationAttributeExportEligibilityStrategy;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandler;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandlerResolver;
import com.mirakl.hybris.core.enums.ProductOrigin;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.product.strategies.McmProductAcceptanceStrategy;
import com.mirakl.hybris.core.product.strategies.ProductExportAttributeValueFormattingStrategy;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

public class MiraklProductDataSheetSyncItemPopulator
    implements Populator<Pair<ProductModel, ProductDataSheetExportContextData>, MiraklProductDataSheetSyncItem> {

  protected ModelService modelService;
  protected ClassificationService classificationService;
  protected MiraklExportCatalogService exportCatalogService;
  protected ClassificationAttributeExportEligibilityStrategy attributeExportEligibilityStrategy;
  protected CoreAttributeHandlerResolver coreAttributeHandlerResolver;
  protected ProductExportAttributeValueFormattingStrategy<Object, String> formattingStrategy;
  protected McmProductAcceptanceStrategy productAcceptanceStrategy;

  @Override
  public void populate(Pair<ProductModel, ProductDataSheetExportContextData> source, MiraklProductDataSheetSyncItem target)
      throws ConversionException {
    ProductModel product = source.getLeft();
    ProductDataSheetExportContextData context = source.getRight();

    target.setProductSku(product.getCode());
    target.setMiraklProductId(product.getMiraklProductId());
    populateAcceptance(source, target);

    if (product.getOrigin() == ProductOrigin.OPERATOR) {
      target.setData(getData(product, context));
    } else if (product.getOrigin() == null) {
      throw new IllegalStateException(format("Product [%s] has no origin.", product.getCode()));
    }
  }

  protected void populateAcceptance(Pair<ProductModel, ProductDataSheetExportContextData> source,
      MiraklProductDataSheetSyncItem target) {
    target.setAcceptance(productAcceptanceStrategy.getAcceptance(source.getLeft()));
  }

  protected Map<String, Object> getData(ProductModel product, ProductDataSheetExportContextData context) {
    Map<String, Object> data = new HashMap<>();
    data.putAll(getCoreAttributesValues(product, context));
    data.putAll(getClassificationAttributesValues(product, context));
    return data;
  }

  protected Map<String, Object> getCoreAttributesValues(ProductModel product, ProductDataSheetExportContextData context) {
    Map<String, Object> data = new HashMap<>();

    if (isEmpty(context.getCoreAttributes())) {
      return data;
    }

    for (Entry<String, PK> entry : context.getCoreAttributes().entrySet()) {
      MiraklCoreAttributeModel coreAttribute = modelService.get(entry.getValue());
      McmCoreAttributeHandler<MiraklCoreAttributeModel> handler = resolveHandler(coreAttribute, context);
      if (coreAttribute.isLocalized()) {
        for (Locale locale : context.getTranslatableLocales()) {
          data.put(exportCatalogService.formatAttributeExportName(entry.getKey(), locale),
              handler.getValue(product, coreAttribute, locale, context));
        }
      } else {
        data.put(entry.getKey(), handler.getValue(product, coreAttribute, context));
      }
    }

    return data;
  }

  protected McmCoreAttributeHandler<MiraklCoreAttributeModel> resolveHandler(MiraklCoreAttributeModel coreAttribute,
      ProductDataSheetExportContextData context) {
    CoreAttributeHandler<MiraklCoreAttributeModel> handler =
        coreAttributeHandlerResolver.determineHandler(coreAttribute, context.getMiraklCatalogSystem());
    if (!(handler instanceof McmCoreAttributeHandler)) {
      throw new IllegalStateException(
          format("Resolved export handler [%s] for attribute [%s] is not an instance of [%s]. No export is possible", handler,
              coreAttribute.getUid(), McmCoreAttributeHandler.class.getSimpleName()));
    }
    return (McmCoreAttributeHandler<MiraklCoreAttributeModel>) handler;
  }

  protected Map<String, Object> getClassificationAttributesValues(ProductModel product,
      ProductDataSheetExportContextData context) {
    Map<String, Object> data = new HashMap<>();

    FeatureList features = classificationService.getFeatures(product);
    for (Feature feature : features) {
      ClassAttributeAssignmentModel assignment = feature.getClassAttributeAssignment();
      if (attributeExportEligibilityStrategy.isExportableAttribute(assignment)) {
        if (shouldBeLocalized(assignment, context)) {
          handleLocalizedClassificationAttribute(context, data, (LocalizedFeature) feature, assignment);
        } else {
          data.put(assignment.getClassificationAttribute().getCode(), getAttributeValueAsString(feature, null));
        }
      }
    }

    return data;
  }

  protected void handleLocalizedClassificationAttribute(ProductDataSheetExportContextData context, Map<String, Object> data,
      LocalizedFeature feature, ClassAttributeAssignmentModel assignment) {
    for (Locale locale : context.getTranslatableLocales()) {
      data.put(exportCatalogService.formatAttributeExportName(assignment.getClassificationAttribute().getCode(), locale),
          getAttributeValueAsString(feature, locale));
    }
  }

  protected boolean shouldBeLocalized(ClassAttributeAssignmentModel assignment, ProductDataSheetExportContextData context) {
    return isTrue(assignment.getLocalized()) && isNotEmpty(context.getTranslatableLocales());
  }

  protected Object getAttributeValueAsString(Feature feature, Locale locale) {
    List<FeatureValue> values =
        feature instanceof LocalizedFeature && locale != null ? ((LocalizedFeature) feature).getValues(locale)
            : feature.getValues();

    if (isTrue(feature.getClassAttributeAssignment().getMultiValued()) && isNotEmpty(values) && values.size() > 1) {
      return getMultiValuedClassificationAttributeValues(values);
    }

    return isNotEmpty(values) ? formattingStrategy.formatValueForExport(values.get(0).getValue()) : null;
  }

  protected List<String> getMultiValuedClassificationAttributeValues(List<FeatureValue> values) {
    List<String> valuesAsString = new ArrayList<>();
    for (FeatureValue featureValue : values) {
      if (featureValue.getValue() != null) {
        if (featureValue.getValue() instanceof ClassificationAttributeValueModel) {
          ClassificationAttributeValueModel classAttributeValue = (ClassificationAttributeValueModel) featureValue.getValue();
          valuesAsString.add(String.valueOf(classAttributeValue.getCode()));
        } else {
          valuesAsString.add(String.valueOf(featureValue.getValue()));
        }
      }
    }
    return valuesAsString;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setClassificationService(ClassificationService classificationService) {
    this.classificationService = classificationService;
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

  @Required
  public void setCoreAttributeHandlerResolver(CoreAttributeHandlerResolver coreAttributeHandlerResolver) {
    this.coreAttributeHandlerResolver = coreAttributeHandlerResolver;
  }

  @Required
  public void setFormattingStrategy(ProductExportAttributeValueFormattingStrategy<Object, String> formattingStrategy) {
    this.formattingStrategy = formattingStrategy;
  }

  @Required
  public void setProductAcceptanceStrategy(McmProductAcceptanceStrategy productAcceptanceStrategy) {
    this.productAcceptanceStrategy = productAcceptanceStrategy;
  }
}
