package com.mirakl.hybris.core.catalog.services.impl;

import static com.google.common.collect.Sets.newHashSet;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateIfSingleResult;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mirakl.hybris.core.catalog.daos.MiraklCoreAttributeDao;
import com.mirakl.hybris.core.catalog.services.MiraklCoreAttributeService;
import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;

public class DefaultMiraklCoreAttributeService implements MiraklCoreAttributeService {

  protected MiraklCoreAttributeDao miraklCoreAttributeDao;
  protected CategoryService categoryService;

  @Override
  public List<MiraklCoreAttributeModel> getUniqueIdentifierCoreAttributes() {
    return miraklCoreAttributeDao.findUniqueIdentifierCoreAttributes();
  }

  @Override
  public List<MiraklCoreAttributeModel> getUniqueIdentifierCoreAttributes(Collection<MiraklCoreAttributeModel> coreAttributes) {
    validateParameterNotNullStandardMessage("coreAttributes", coreAttributes);
    if (isEmpty(coreAttributes)) {
      return Collections.emptyList();
    }

    return FluentIterable.from(coreAttributes).filter(new Predicate<MiraklCoreAttributeModel>() {

      @Override
      public boolean apply(MiraklCoreAttributeModel attribute) {
        return attribute.isUniqueIdentifier();
      }
    }).toList();
  }

  @Override
  public List<MiraklCoreAttributeModel> getVariantCoreAttributes() {
    return miraklCoreAttributeDao.findVariantCoreAttributes();
  }

  @Override
  public List<MiraklCoreAttributeModel> getVariantCoreAttributes(Collection<MiraklCoreAttributeModel> coreAttributes) {
    validateParameterNotNullStandardMessage("coreAttributes", coreAttributes);
    if (isEmpty(coreAttributes)) {
      return Collections.emptyList();
    }

    return FluentIterable.from(coreAttributes).filter(new Predicate<MiraklCoreAttributeModel>() {

      @Override
      public boolean apply(MiraklCoreAttributeModel attribute) {
        return attribute.isVariant();
      }
    }).toList();
  }

  @Override
  public List<MiraklCoreAttributeModel> getAttributesForRole(MiraklAttributeRole attributeRole) {
    validateParameterNotNullStandardMessage("attributeRole", attributeRole);

    return miraklCoreAttributeDao.findCoreAttributeByRole(attributeRole);
  }

  @Override
  public List<MiraklCoreAttributeModel> getAttributesForRole(final MiraklAttributeRole attributeRole,
      final Collection<MiraklCoreAttributeModel> coreAttributes) {
    validateParameterNotNullStandardMessage("attributeRole", attributeRole);
    validateParameterNotNullStandardMessage("coreAttributes", coreAttributes);

    if (isEmpty(coreAttributes)) {
      return Collections.emptyList();
    }

    return FluentIterable.from(coreAttributes).filter(new Predicate<MiraklCoreAttributeModel>() {

      @Override
      public boolean apply(MiraklCoreAttributeModel attribute) {
        return attributeRole.equals(attribute.getRole());
      }
    }).toList();
  }

  @Override
  public List<MiraklCategoryCoreAttributeModel> getAllCategoryCoreAttributes(
      final Collection<MiraklCoreAttributeModel> coreAttributes) {
    validateParameterNotNullStandardMessage("coreAttributes", coreAttributes);

    if (isEmpty(coreAttributes)) {
      return Collections.emptyList();
    }

    List<MiraklCategoryCoreAttributeModel> categoryAttributes = new ArrayList<>();
    for (MiraklCoreAttributeModel attribute : coreAttributes) {
      if (attribute instanceof MiraklCategoryCoreAttributeModel) {
        categoryAttributes.add((MiraklCategoryCoreAttributeModel) attribute);
      }
    }

    return ImmutableList.copyOf(categoryAttributes);
  }

  @Override
  public MiraklCategoryCoreAttributeModel getCategoryCoreAttributeForRole(MiraklAttributeRole attributeRole,
      MiraklCoreAttributeConfigurationModel coreAttributeConfiguration) {
    validateParameterNotNullStandardMessage("attributeRole", attributeRole);
    validateParameterNotNullStandardMessage("coreAttributeConfiguration", coreAttributeConfiguration);

    List<MiraklCoreAttributeModel> categoryRoleAttributes =
        getAttributesForRole(attributeRole, coreAttributeConfiguration.getCoreAttributes());

    validateIfSingleResult(categoryRoleAttributes, MiraklCategoryCoreAttributeModel.class, "role", attributeRole);

    return (MiraklCategoryCoreAttributeModel) categoryRoleAttributes.get(0);
  }

  @Override
  public Map<String, Set<CategoryModel>> getAllCategoryValuesForCategoryCoreAttributes(Collection<MiraklCoreAttributeModel> coreAttributes,
      CatalogVersionModel catalogVersion) {
    List<MiraklCategoryCoreAttributeModel> allCategoryCoreAttributes = getAllCategoryCoreAttributes(coreAttributes);
    if (isNotEmpty(allCategoryCoreAttributes)) {
      Map<String, Set<CategoryModel>> allCategoryValues = new HashMap<>();
      for (MiraklCategoryCoreAttributeModel categoryAttribute : allCategoryCoreAttributes) {
        CategoryModel rootCategory = categoryService.getCategoryForCode(catalogVersion, categoryAttribute.getRootCategoryCode());
        allCategoryValues.put(categoryAttribute.getUid(), newHashSet(rootCategory.getAllSubcategories()));
      }
      return ImmutableMap.copyOf(allCategoryValues);
    }

    return Collections.emptyMap();
  }

  @Override
  public Map<String, MiraklCoreAttributeModel> getCoreAttributeCodes(Set<MiraklCoreAttributeModel> allCoreAttributes) {
    Map<String, MiraklCoreAttributeModel> coreAttributes = new HashMap<>();
    if (isNotEmpty(allCoreAttributes)) {
      for (MiraklCoreAttributeModel coreAttribute : allCoreAttributes) {
        coreAttributes.put(coreAttribute.getCode(), coreAttribute);
      }
    }
    return coreAttributes;
  }

  @Override
  public List<MiraklCoreAttributeModel> getAllCoreAttributes() {
    return miraklCoreAttributeDao.find();
  }

  @Required
  public void setMiraklCoreAttributeDao(MiraklCoreAttributeDao miraklCoreAttributeDao) {
    this.miraklCoreAttributeDao = miraklCoreAttributeDao;
  }

  @Required
  public void setCategoryService(CategoryService categoryService) {
    this.categoryService = categoryService;
  }
}
