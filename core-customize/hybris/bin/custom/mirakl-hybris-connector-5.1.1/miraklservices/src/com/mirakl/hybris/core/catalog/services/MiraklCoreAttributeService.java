package com.mirakl.hybris.core.catalog.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.model.MiraklCategoryCoreAttributeModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeConfigurationModel;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public interface MiraklCoreAttributeService {

  /**
   * Returns all Mirakl Core attributes flagged as unique identifiers
   *
   * @return a list of all unique identifier Mirakl Core Attributes
   */
  List<MiraklCoreAttributeModel> getUniqueIdentifierCoreAttributes();

  /**
   * Fetches inside a Collection the Mirakl Core attributes flagged as unique identifiers
   * 
   * @param coreAttributes the core attributes in which to search
   * @return a list of Mirakl Core Attributes
   */
  List<MiraklCoreAttributeModel> getUniqueIdentifierCoreAttributes(Collection<MiraklCoreAttributeModel> coreAttributes);

  /**
   * Returns all Mirakl Core Attributes flagged as variant attributes
   *
   * @return a list of all variant Mirakl Core Attributes
   */
  List<MiraklCoreAttributeModel> getVariantCoreAttributes();

  /**
   * Fetches inside a Collection the Mirakl Core attributes flagged as variant attributes
   * 
   * @param coreAttributes the core attributes in which to search
   * @return a list of Mirakl Core Attributes
   */
  List<MiraklCoreAttributeModel> getVariantCoreAttributes(Collection<MiraklCoreAttributeModel> coreAttributes);

  /**
   * Returns all Mirakl core attributes having a given role
   * 
   * @param attributeRole the role to search for
   * @return all core attributes having the matching role
   */
  List<MiraklCoreAttributeModel> getAttributesForRole(MiraklAttributeRole attributeRole);

  /**
   * Fetches inside a Collection the Mirakl Core attributes having a given role
   * 
   * @param attributeRole the role to search for
   * @param coreAttributes the core attributes in which to search
   * @return the core attributes having the matching role
   */
  List<MiraklCoreAttributeModel> getAttributesForRole(MiraklAttributeRole attributeRole,
      Collection<MiraklCoreAttributeModel> coreAttributes);

  /**
   * Searches, within a core attribute configuration, for a {@link MiraklCategoryCoreAttributeModel} matching a given role.
   * 
   * @param role the role to be searched for.
   * @param coreAttributeConfiguration the configuration to search inside
   * @return a category core attribute matching the role
   * 
   * @throws IllegalStateException if the matching attribute is not instance of {@link MiraklCategoryCoreAttributeModel}
   * @throws AmbiguousIdentifierException if more than one matching attribute was found
   * @throws UnknownIdentifierException if unable to find a matching attribute for the role
   */
  MiraklCategoryCoreAttributeModel getCategoryCoreAttributeForRole(MiraklAttributeRole role,
      MiraklCoreAttributeConfigurationModel coreAttributeConfiguration);


  /**
   * Searches inside a Collection for all {@link MiraklCategoryCoreAttributeModel}s
   * 
   * @param coreAttributes the collection to search inside
   * @return a list of all category core attributes
   */
  List<MiraklCategoryCoreAttributeModel> getAllCategoryCoreAttributes(Collection<MiraklCoreAttributeModel> coreAttributes);

  /**
   * Returns all the Mirakl core attributes
   *
   * @return all the Mirakl core Attributes
   */
  List<MiraklCoreAttributeModel> getAllCoreAttributes();

  /**
   * Extracts all Category core attributes from a Collection of core attributes and returns all possible values for each extracted
   * attribute
   * 
   * @param coreAttributes the core attributes to examine
   * @param catalogVersion the catalog version to be used
   * @return a map associating a uid of a {@link MiraklCategoryCoreAttributeModel} to all its possible category values
   */
  Map<String, Set<CategoryModel>> getAllCategoryValuesForCategoryCoreAttributes(
      Collection<MiraklCoreAttributeModel> coreAttributes, CatalogVersionModel catalogVersion);

  /**
   * Returns a map associating core attributes to their codes
   * 
   * @param allCoreAttributes
   * @return a map having for value the core attribute and for key its code
   */
  Map<String, MiraklCoreAttributeModel> getCoreAttributeCodes(Set<MiraklCoreAttributeModel> allCoreAttributes);



}
