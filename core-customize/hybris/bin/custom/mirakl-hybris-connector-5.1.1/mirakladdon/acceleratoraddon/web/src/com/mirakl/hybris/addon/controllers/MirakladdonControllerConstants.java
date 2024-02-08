package com.mirakl.hybris.addon.controllers;

import com.mirakl.hybris.addon.model.*;

public interface MirakladdonControllerConstants {
  String ADDON_PREFIX = "addon:/mirakladdon/";
  String CMS_PREFIX = "/view/";
  String CMS_SUFFIX = "Controller";

  interface Views {
    interface Pages {
      interface MultiStepCheckout {
        String ChooseDeliveryMethodPage = ADDON_PREFIX + "pages/checkout/multi/chooseDeliveryMethodPage";
      }
    }
  }

  interface Cms {
    String CMSBuyBoxComponent = CMS_PREFIX + CMSBuyBoxComponentModel._TYPECODE + CMS_SUFFIX;
    String CMSProductDetailsComponent = CMS_PREFIX + CMSProductDetailsComponentModel._TYPECODE + CMS_SUFFIX;
    String CMSTabOfferListComponent = CMS_PREFIX + CMSTabOfferListComponentModel._TYPECODE + CMS_SUFFIX;
    String MiraklCMSProductListComponent = CMS_PREFIX + MiraklCMSProductListComponentModel._TYPECODE + CMS_SUFFIX;
    String MiraklProductGridComponent = CMS_PREFIX + MiraklProductGridComponentModel._TYPECODE + CMS_SUFFIX;
    String MiraklSearchResultsListComponent = CMS_PREFIX + MiraklSearchResultsListComponentModel._TYPECODE + CMS_SUFFIX;
    String MiraklSearchResultsGridComponent = CMS_PREFIX + MiraklSearchResultsGridComponentModel._TYPECODE + CMS_SUFFIX;
  }

  interface Fragments {
    interface Shop {
      String shopEvaluationFragment = ADDON_PREFIX + "fragments/shop/shopEvaluationsTab";
    }
    interface Order {
      String orderIncidentPopup = ADDON_PREFIX + "fragments/order/orderIncidentPopup";
    }
    interface Inbox {
      String threadListLinesFragment = ADDON_PREFIX + "fragments/inbox/threadListLines";
    }
  }
}
