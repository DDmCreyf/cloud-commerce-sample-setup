package com.mirakl.hybris.backofficeaddon.renderers;

import org.springframework.beans.factory.annotation.Required;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Span;

import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.common.AbstractWidgetComponentRenderer;
import com.mirakl.hybris.core.enums.ProductOrigin;

import de.hybris.platform.core.model.product.ProductModel;

public class MarketplaceOriginRenderer extends AbstractWidgetComponentRenderer<Component, Object, ProductModel> {

  protected static final String YW_IMAGE_ATTRIBUTE_IS_MARKETPLACE = "yw-image-attribute-is-marketplace";
  protected static final String YW_IMAGE_ATTRIBUTE_IS_MARKETPLACE_ICON = "yw-image-attribute-is-marketplace-icon";
  protected static final String ORIGIN_LABEL =
      ProductModel.ORIGIN.substring(0, 1).toUpperCase() + ProductModel.ORIGIN.substring(1);

  protected LabelService labelService;

  /**
   * Renders the Mirakl Icon in the Backoffice Product Cockpit if the given Product Origin is ProductOrigin.MARKETPLACE.
   *
   * @param parent
   * @param configuration
   * @param data
   * @param dataType
   * @param widgetInstanceManager
   */
  @Override
  public void render(Component parent, Object configuration, ProductModel data, DataType dataType,
      WidgetInstanceManager widgetInstanceManager) {
    if (ProductOrigin.MARKETPLACE.equals(data.getOrigin())) {
      // Creates a span to encapsulate the data
      Span icon = new Span();
      // Add the css to the span
      UITools.addSClass(icon, YW_IMAGE_ATTRIBUTE_IS_MARKETPLACE);
      UITools.addSClass(icon, YW_IMAGE_ATTRIBUTE_IS_MARKETPLACE_ICON);
      icon.setTooltiptext(String.format("%s : %s", ORIGIN_LABEL, labelService.getObjectLabel(ProductOrigin.MARKETPLACE)));
      parent.appendChild(icon);
      labelService.getObjectLabel(ProductOrigin.MARKETPLACE);
      fireComponentRendered(parent, configuration, data);
    }
  }

  @Required
  public void setLabelService(LabelService labelService) {
    this.labelService = labelService;
  }

}
