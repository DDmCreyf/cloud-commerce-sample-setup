package com.mirakl.hybris.core.catalog.strategies.impl;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ProductImportData;
import com.mirakl.hybris.beans.ProductImportFileContextData;
import com.mirakl.hybris.core.catalog.services.MiraklExportCatalogContext;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandler;
import com.mirakl.hybris.core.catalog.strategies.CoreAttributeHandlerResolver;
import com.mirakl.hybris.core.enums.MiraklAttributeRole;
import com.mirakl.hybris.core.enums.MiraklCatalogSystem;
import com.mirakl.hybris.core.model.MiraklCoreAttributeModel;

import de.hybris.platform.core.Registry;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultCoreAttributeHandlerResolver implements CoreAttributeHandlerResolver {

  protected Map<MiraklCatalogSystem, Map<String, CoreAttributeHandler<?>>> aliasHandlersPerCatalogSystem;
  protected Map<MiraklCatalogSystem, Map<MiraklAttributeRole, CoreAttributeHandler<?>>> roleAttributeHandlersPerCatalogSystem;
  protected Map<MiraklCatalogSystem, CoreAttributeHandler<?>> defaultHandlerPerCatalogSystem;
  protected Map<MiraklAttributeRole, CoreAttributeHandler<?>> defaultRoleAttributeHandlers;
  protected CoreAttributeHandler defaultHandler;

  @Override
  public <T extends MiraklCoreAttributeModel> CoreAttributeHandler<T> determineHandler(MiraklCoreAttributeModel attribute,
      ProductImportData data, ProductImportFileContextData context) {
    return determineHandler(attribute, context.getGlobalContext().getMiraklCatalogSystem());
  }

  @Override
  public <T extends MiraklCoreAttributeModel> CoreAttributeHandler<T> determineHandler(MiraklCoreAttributeModel attribute,
      MiraklExportCatalogContext context) {
    return determineHandler(attribute, context.getExportConfig().getMiraklCatalogSystem());
  }

  @Override
  public <T extends MiraklCoreAttributeModel> CoreAttributeHandler<T> determineHandler(MiraklCoreAttributeModel attribute,
      MiraklCatalogSystem miraklCatalogSystem) {

    String importExportHandlerStringId = attribute.getImportExportHandlerStringId();
    if (isNotBlank(importExportHandlerStringId)) {
      CoreAttributeHandler<T> registeredHandler =
          (CoreAttributeHandler<T>) lookupForRegisteredHandlerForAlias(importExportHandlerStringId, miraklCatalogSystem);
      return registeredHandler != null ? registeredHandler : getCoreAttributeBeanHandler(attribute);
    }

    if (attribute.getRole() != null) {
      CoreAttributeHandler<T> registeredHandler = (CoreAttributeHandler<T>)lookupForRegisteredHandlerForRole(attribute.getRole(), miraklCatalogSystem);
      if (registeredHandler != null) {
        return registeredHandler;
      }
      if (defaultRoleAttributeHandlers.containsKey(attribute.getRole())) {
        return (CoreAttributeHandler<T>) defaultRoleAttributeHandlers.get(attribute.getRole());
      }
    }

    if (miraklCatalogSystem != null && defaultHandlerPerCatalogSystem.containsKey(miraklCatalogSystem)) {
      return (CoreAttributeHandler<T>) defaultHandlerPerCatalogSystem.get(miraklCatalogSystem);
    }

    return defaultHandler;
  }

  protected CoreAttributeHandler<?> lookupForRegisteredHandlerForAlias(String importExportHandlerStringId,
      MiraklCatalogSystem miraklCatalogSystem) {
    if (miraklCatalogSystem != null && aliasHandlersPerCatalogSystem.containsKey(miraklCatalogSystem)) {
      return aliasHandlersPerCatalogSystem.get(miraklCatalogSystem).get(importExportHandlerStringId);
    }
    return null;
  }

  protected CoreAttributeHandler<?> lookupForRegisteredHandlerForRole(MiraklAttributeRole role,
      MiraklCatalogSystem miraklCatalogSystem) {
    if (miraklCatalogSystem != null && roleAttributeHandlersPerCatalogSystem.containsKey(miraklCatalogSystem)) {
      return roleAttributeHandlersPerCatalogSystem.get(miraklCatalogSystem).get(role);
    }
    return null;
  }

  protected <T extends MiraklCoreAttributeModel> CoreAttributeHandler<T> getCoreAttributeBeanHandler(
      MiraklCoreAttributeModel attribute) {
    return Registry.getApplicationContext().getBean(attribute.getImportExportHandlerStringId(), CoreAttributeHandler.class);
  }

  @Required
  public void setAliasHandlersPerCatalogSystem(
      Map<MiraklCatalogSystem, Map<String, CoreAttributeHandler<?>>> aliasHandlersPerCatalogSystem) {
    this.aliasHandlersPerCatalogSystem = aliasHandlersPerCatalogSystem;
  }

  @Required
  public void setRoleAttributeHandlersPerCatalogSystem(
      Map<MiraklCatalogSystem, Map<MiraklAttributeRole, CoreAttributeHandler<?>>> roleAttributeHandlersPerCatalogSystem) {
    this.roleAttributeHandlersPerCatalogSystem = roleAttributeHandlersPerCatalogSystem;
  }

  @Required
  public void setDefaultHandlerPerCatalogSystem(
      Map<MiraklCatalogSystem, CoreAttributeHandler<?>> defaultHandlerPerCatalogSystem) {
    this.defaultHandlerPerCatalogSystem = defaultHandlerPerCatalogSystem;
  }

  @Required
  public void setDefaultRoleAttributeHandlers(Map<MiraklAttributeRole, CoreAttributeHandler<?>> defaultRoleAttributeHandlers) {
    this.defaultRoleAttributeHandlers = defaultRoleAttributeHandlers;
  }

  @Required
  public void setDefaultHandler(CoreAttributeHandler defaultHandler) {
    this.defaultHandler = defaultHandler;
  }

}
