package com.mirakl.hybris.core.product.populators;

import static com.google.common.collect.Maps.transformEntries;
import static com.google.common.collect.Sets.intersection;
import static com.mirakl.hybris.core.util.DataModelUtils.extractAttributeQualifiers;
import static com.mirakl.hybris.core.util.DataModelUtils.transformMapCollectionValuesToPks;
import static com.mirakl.hybris.core.util.DataModelUtils.transformMapComposedTypeKeyToCode;
import static com.mirakl.hybris.core.util.DataModelUtils.transformMapValuesToPks;
import static java.lang.Runtime.getRuntime;
import static org.apache.commons.collections.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mirakl.hybris.beans.ProductImportGlobalContextData;
import com.mirakl.hybris.core.catalog.services.MiraklCoreAttributeService;
import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklProductImportCronJobModel;
import com.mirakl.hybris.core.product.services.MiraklProductService;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.variants.model.VariantTypeModel;

public class ProductImportGlobalContextDataPopulator
    implements Populator<MiraklProductImportCronJobModel, ProductImportGlobalContextData> {

  protected VariantsService variantsService;
  protected MiraklCoreAttributeService coreAttributeService;
  protected CategoryService categoryService;
  protected MiraklProductService miraklProductService;

  @Override
  public void populate(MiraklProductImportCronJobModel source, ProductImportGlobalContextData target) throws ConversionException {
    populateAllCategoryValues(source, target);
    target.setForceProductUpdate(source.isForceProductUpdate());
    target.setInputFilePattern(source.getInputFilePattern());
    target.setProductCatalogVersion(source.getCatalogVersion().getPk());
    target.setNumberOfWorkers(
        source.getNumberOfWorkers() == null ? getRuntime().availableProcessors() : source.getNumberOfWorkers());
    Collection<VariantTypeModel> allVariantTypes = variantsService.getAllVariantTypes();
    Map<ComposedTypeModel, Set<String>> variantAttributesPerType = getVariantAttributesPerType(allVariantTypes);
    target.setVariantAttributesPerType(transformMapComposedTypeKeyToCode(variantAttributesPerType));
    target.setDeclaredVariantAttributesPerType(
        transformMapComposedTypeKeyToCode(getDeclaredVariantAttributesPerType(variantAttributesPerType)));
    String rootProductType = getRootProductType(source);
    target.setRootProductType(rootProductType);
    target.setVariantTypeHierarchyPerType(populateVariantTypeHierarchyPerType(allVariantTypes, rootProductType));
    Map<ComposedTypeModel, Set<String>> attributeDescriptorQualifiersPerType =
        miraklProductService.getAttributeDescriptorQualifiersPerProductType();
    target.setAttributesPerType(transformMapComposedTypeKeyToCode((attributeDescriptorQualifiersPerType)));
    Map<String, MiraklCoreAttributeModel> coreAttributeCodes = coreAttributeService.getCoreAttributeCodes(source.getCoreAttributes());
    target.setCoreAttributes(transformMapValuesToPks(coreAttributeCodes));
    target.setCoreAttributePerRole(getCoreAttributesPerRole(source.getCoreAttributes()));
    target.setVariantAttributes(getVariantCoreAttributeCodes(source));
    target.setUniqueIdentifierCoreAttributes(getUniqueIdentifierCoreAttributesCodes(source.getCoreAttributes()));
    target.setCategoryRoleAttribute(coreAttributeService
        .getCategoryCoreAttributeForRole(MiraklAttributeRole.CATEGORY_ATTRIBUTE, source.getCoreAttributeConfiguration()).getPk());
    target.setMiraklCatalogSystem(source.getCatalogVersion().getCatalog().getMiraklCatalogSystem());
    if (isNotEmpty(source.getMediaDownloadHttpHeaders())) {
      target.setMediaDownloadHttpHeaders(ImmutableMap.copyOf(source.getMediaDownloadHttpHeaders()));
    }
  }

  protected void populateAllCategoryValues(MiraklProductImportCronJobModel source, ProductImportGlobalContextData target) {
    Map<String, Set<CategoryModel>> allCategoryValues = coreAttributeService
        .getAllCategoryValuesForCategoryCoreAttributes(source.getCoreAttributeConfiguration().getCoreAttributes(), source.getCatalogVersion());
    target.setAllCategoryValues(transformMapCollectionValuesToPks(allCategoryValues));
  }

  protected Map<ComposedTypeModel, Set<String>> getVariantAttributesPerType(Collection<VariantTypeModel> allVariantTypes) {
    Map<ComposedTypeModel, Set<String>> result = new HashMap<>();
    for (ComposedTypeModel composedType : allVariantTypes) {
      if (isTrue(composedType.getAbstract())) {
        continue;
      }
      if (composedType instanceof VariantTypeModel) {
        result.put(composedType, ImmutableSet.copyOf(variantsService.getVariantAttributes(composedType.getCode())));
      } else {
        result.put(composedType, Collections.<String>emptySet());
      }
    }
    return ImmutableMap.copyOf(result);
  }

  protected Set<String> getUniqueIdentifierCoreAttributesCodes(Set<MiraklCoreAttributeModel> allCoreAttributes) {
    Set<String> uniqueIdentifiersAttributes = new HashSet<>();
    for (MiraklCoreAttributeModel coreAttribute : allCoreAttributes) {
      if (coreAttribute.isUniqueIdentifier()) {
        uniqueIdentifiersAttributes.add(coreAttribute.getCode());
      }
    }
    return uniqueIdentifiersAttributes;
  }

  protected Map<ComposedTypeModel, Set<String>> getDeclaredVariantAttributesPerType(
      Map<ComposedTypeModel, Set<String>> variantAttributesPerType) {
    return transformEntries(variantAttributesPerType, new Maps.EntryTransformer<ComposedTypeModel, Set<String>, Set<String>>() {

      @Override
      public Set<String> transformEntry(ComposedTypeModel composedTypes, Set<String> allVariantAttributes) {
        return intersection(allVariantAttributes, extractAttributeQualifiers(composedTypes.getDeclaredattributedescriptors()));
      }
    });
  }

  protected List<String> getVariantCoreAttributeCodes(MiraklProductImportCronJobModel source) {
    ArrayList<String> variantCoreAttributeCodes = new ArrayList<>();
    Set<MiraklCoreAttributeModel> coreAttributes = source.getCoreAttributes();
    for (MiraklCoreAttributeModel coreAttribute : coreAttributes) {
      if (coreAttribute.isVariant()) {
        variantCoreAttributeCodes.add(coreAttribute.getCode());
      }
    }
    return variantCoreAttributeCodes;
  }

  protected String getRootProductType(MiraklProductImportCronJobModel source) {
    ComposedTypeModel rootProductType = source.getCatalogVersion().getCatalog().getRootProductType();
    if (rootProductType == null) {
      return ProductModel._TYPECODE;
    }
    return rootProductType.getCode();
  }

  protected Map<String, List<String>> populateVariantTypeHierarchyPerType(Collection<VariantTypeModel> allVariantTypes,
      String rootProductType) {
    Map<String, List<String>> result = new HashMap<>();
    for (VariantTypeModel variantType : allVariantTypes) {
      result.put(variantType.getCode(), populateVariantTypeBranch(variantType, rootProductType));
    }
    return ImmutableMap.copyOf(result);
  }

  protected List<String> populateVariantTypeBranch(VariantTypeModel variantType, String rootProductType) {
    ComposedTypeModel currentType = variantType;
    List<String> variantBranch = new ArrayList<>();

    while (currentType instanceof VariantTypeModel) {
      if (isFalse(currentType.getAbstract())) {
        variantBranch.add(currentType.getCode());
      }
      currentType = currentType.getSuperType();
    }
    variantBranch.add(rootProductType);

    return ImmutableList.copyOf(Lists.reverse(variantBranch));
  }

  protected Map<MiraklAttributeRole, String> getCoreAttributesPerRole(Set<MiraklCoreAttributeModel> allCoreAttributes) {
    Map<MiraklAttributeRole, String> result = new EnumMap<>(MiraklAttributeRole.class);
    for (MiraklCoreAttributeModel attribute : allCoreAttributes) {
      if (attribute.getRole() != null) {
        result.put(attribute.getRole(), attribute.getCode());
      }
    }

    return ImmutableMap.copyOf(result);
  }

  @Required
  public void setVariantsService(VariantsService variantsService) {
    this.variantsService = variantsService;
  }

  @Required
  public void setCoreAttributeService(MiraklCoreAttributeService coreAttributeService) {
    this.coreAttributeService = coreAttributeService;
  }

  @Required
  public void setCategoryService(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @Required
  public void setMiraklProductService(MiraklProductService miraklProductService) {
    this.miraklProductService = miraklProductService;
  }
}
