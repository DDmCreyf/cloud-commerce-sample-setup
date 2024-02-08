package com.mirakl.hybris.occtests.setup;

import java.util.List;

import de.hybris.platform.commercewebservicestests.setup.CommercewebservicesTestSetup;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetupContext;

public class MiraklwebservicesTestSetup extends CommercewebservicesTestSetup {

  public static final String WS_TEST = "wsTest";

  @Override
  public void createProjectData(final SystemSetupContext context) {
    super.createProjectData(context);
    loadMiraklOccData();
  }

  protected void loadMiraklOccData() {
    final List<String> extensionNames = Registry.getCurrentTenant().getTenantSpecificExtensionNames();
    if (extensionNames.contains("miraklocc")) {
      getSetupImpexService().importImpexFile("/miraklocctests/import/coredata/stores/wsTest/solr.impex", true, false);
      getSetupImpexService().importImpexFile("/miraklocctests/import/coredata/stores/wsTest/shops.impex", true, false);
      getSetupImpexService()
          .importImpexFile("/miraklocctests/import/sampledata/contentCatalogs/wsTestProductCatalog/products.impex", true, false);
      getSetupImpexService()
          .importImpexFile("/miraklocctests/import/sampledata/contentCatalogs/wsTestProductCatalog/offers.impex", true, false);
      getSetupSyncJobService().executeCatalogSyncJob(String.format("%sProductCatalog", WS_TEST));
      getSetupSolrIndexerService().executeSolrIndexerCronJob(String.format("%sIndex", WS_TEST), true);
    }
  }

}
