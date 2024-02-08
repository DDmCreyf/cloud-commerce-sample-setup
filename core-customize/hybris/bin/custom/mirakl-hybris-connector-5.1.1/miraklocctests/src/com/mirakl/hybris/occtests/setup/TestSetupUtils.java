package com.mirakl.hybris.occtests.setup;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.facades.product.OfferFacade;
import de.hybris.platform.core.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TestSetupUtils {
  private static final Logger LOG = LoggerFactory.getLogger(TestSetupUtils.class);

  // Called in buildcallbacks.xml
  public static void loadExtensionDataInJunit() throws Exception {
    Registry.setCurrentTenantByID("junit");
    loadData();
  }

  private static void loadMiraklData() {
    final MiraklwebservicesTestSetup miraklwebservicesTestSetup =
        Registry.getApplicationContext().getBean("miraklwebservicesTestSetup", MiraklwebservicesTestSetup.class);
    miraklwebservicesTestSetup.loadMiraklOccData();
  }

  public static void loadData() throws Exception {
    if (shouldLoadData()) {
      loadMiraklData();
    } else {
      LOG.info("Mirakl data are already loaded");
    }
  }

  private static boolean shouldLoadData() {
    final OfferFacade offerFacade = Registry.getApplicationContext().getBean("offerFacade", OfferFacade.class);
    final List<OfferData> offerDataList = offerFacade.getOffersForProductCode("miraklProduct1");
    return offerDataList == null || offerDataList.isEmpty();
  }

}
