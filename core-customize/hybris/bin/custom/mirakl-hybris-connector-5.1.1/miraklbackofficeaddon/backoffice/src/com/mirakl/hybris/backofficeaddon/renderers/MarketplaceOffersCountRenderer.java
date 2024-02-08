package com.mirakl.hybris.backofficeaddon.renderers;

import com.mirakl.hybris.core.enums.ProductOrigin;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Span;

import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.common.AbstractWidgetComponentRenderer;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.platform.core.model.product.ProductModel;

public class MarketplaceOffersCountRenderer extends AbstractWidgetComponentRenderer<Component, Object, ProductModel> {

  protected static final String ZUL_WGT_DIV = "zul.wgt.Div";
  protected static final String ZUL_SEL_LISTCELL = "zul.sel.Listcell";
  protected static final String YW_LISTVIEW_CELL_LABEL = "yw-listview-cell-label";
  protected static final String YW_GRIDVIEW_MARKETPLACE_AMOUNT = "yw-gridview-marketplace-amount";
  protected static final String YW_GRIDVIEW_MARKETPLACE_AMOUNT_MARGIN_LEFT = "yw-gridview-marketplace-amount-margin-left";

  protected OfferService offerService;

  /**
   * Renders both ListView and GridView for the given Product following the parent WidgetClass.
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
    int offersQuantity = offerService.countOffersForProduct(data.getCode());
    if (offersQuantity > 0 && ZUL_WGT_DIV.equals(parent.getWidgetClass())) {
      rendererGridView(parent, configuration, data, offersQuantity);
    } else if (ZUL_SEL_LISTCELL.equals(parent.getWidgetClass())) {
      rendererListView(parent, configuration, data, offersQuantity);
    }
  }

  protected void rendererListView(Component parent, Object configuration, ProductModel data, int offersQuantity) {
    // Creates a span to encapsulate the data
    Span span = new Span();
    span.setTooltiptext(String.format("%s : %s", offersQuantity, getTooltipText()));
    // Add the css to the span
    UITools.addSClass(span, YW_LISTVIEW_CELL_LABEL);
    // Creates a label that contains the data (in fact this will a span with a z-class="label")
    Label label = new Label();
    label.setValue(Integer.toString(offersQuantity));
    span.appendChild(label);
    parent.appendChild(span);
    fireComponentRendered(parent, configuration, data);
  }

  protected void rendererGridView(Component parent, Object configuration, ProductModel data, int offersQuantity) {
    // Creates a span to encapsulate the data
    Span span = new Span();
    span.setTooltiptext(String.format("%s: %s", getTooltipText(), offersQuantity));
    // Add the css to the span
    if(ProductOrigin.MARKETPLACE.equals(data.getOrigin())){
      UITools.addSClass(span, YW_GRIDVIEW_MARKETPLACE_AMOUNT);
    } else {
      UITools.addSClass(span, YW_GRIDVIEW_MARKETPLACE_AMOUNT_MARGIN_LEFT);
    }
    // Creates a label that contains the data (in fact this will a span with a z-class="html")
    Html html = new Html();
    html.setContent(Integer.toString(offersQuantity));
    span.appendChild(html);
    parent.appendChild(span);
    fireComponentRendered(parent, configuration, data);
  }

  protected String getTooltipText() {
    return Labels.getLabel("product.tooltip.offers");
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

}
