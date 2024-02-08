package com.mirakl.hybris.core.catalog.interceptors;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.type.TypeService;

public class MiraklCatalogValidateInterceptor implements ValidateInterceptor<CatalogModel> {

  protected TypeService typeService;

  @Override
  public void onValidate(final CatalogModel catalog, final InterceptorContext context) throws InterceptorException {
    ComposedTypeModel rootProductType = catalog.getRootProductType();

    if (rootProductType != null
        && !typeService.isAssignableFrom(typeService.getComposedTypeForCode(ProductModel._TYPECODE), rootProductType)) {
      throw new InterceptorException(
          format("Root product type ([%s]) must be a subtype of [%s]", rootProductType.getCode(), ProductModel._TYPECODE));
    }
  }

  @Required
  public void setTypeService(TypeService typeService) {
    this.typeService = typeService;
  }
}
