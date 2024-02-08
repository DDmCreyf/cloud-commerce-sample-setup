package com.mirakl.hybris.advancedpricing.backoffice.editor;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;

import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchInitContext;
import com.hybris.cockpitng.core.config.CockpitConfigurationException;
import com.hybris.cockpitng.core.config.impl.DefaultConfigContext;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.AdvancedSearch;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.editors.CockpitEditorRenderer;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.OfferPricingModel;

public class OfferPriceFinderEditor extends AbstractComponentWidgetAdapterAware implements CockpitEditorRenderer<Object> {

  private static final Logger LOG = Logger.getLogger(OfferPriceFinderEditor.class);

  public void render(Component parent, final EditorContext<Object> ctx, EditorListener<Object> warehouseEditorListener) {
    Div cnt = new Div();
    Button button = new Button(Labels.getLabel("hmc.findofferprices"));

    button.addEventListener("onClick", new EventListener<Event>() {
      public void onEvent(Event event) {
        AdvancedSearchData searchData = new AdvancedSearchData();
        searchData.setTypeCode("OfferPricing");
        WidgetInstanceManager wim = (WidgetInstanceManager) ctx.getParameter("wim");
        searchData.setGlobalOperator(ValueComparisonOperator.AND);
        AdvancedSearchInitContext initContext = createSearchContext(searchData, wim, ctx);
        sendOutput("finderOutput", initContext);
      }
    });

    parent.appendChild(cnt);
    cnt.appendChild(button);
  }

  protected AdvancedSearchInitContext createSearchContext(AdvancedSearchData searchData, WidgetInstanceManager wim,
      EditorContext<Object> ctx) {
    AdvancedSearch config = loadAdvancedConfiguration(wim, "offerid-advanced-search");
    for (FieldType fieldType : config.getFieldList().getField()) {
      if (OfferPricingModel.OFFERID.equals(fieldType.getName())) {
        OfferModel currentOffer = (OfferModel) ctx.getParameter("parentObject");
        searchData.addCondition(fieldType, ValueComparisonOperator.EQUALS, currentOffer.getId());
        fieldType.setDisabled(Boolean.TRUE);
        return new AdvancedSearchInitContext(searchData, config);
      }
    }

    LOG.error("Impossible to create search context.");
    return null;
  }

  protected AdvancedSearch loadAdvancedConfiguration(WidgetInstanceManager wim, String name) {
    DefaultConfigContext context = new DefaultConfigContext(name, "OfferPricing");

    try {
      return wim.loadConfiguration(context, AdvancedSearch.class);
    } catch (CockpitConfigurationException e) {
      LOG.error("Failed to load advanced configuration.", e);
      return null;
    }
  }

}
