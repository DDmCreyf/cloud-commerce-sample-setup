package com.mirakl.hybris.core.product.strategies.impl;

import static com.mirakl.hybris.core.constants.MiraklcatalogintegratorConstants.*;
import static com.mirakl.hybris.core.enums.MiraklExportType.PRODUCT_EXPORT;
import static com.mirakl.hybris.core.util.PaginationUtils.getNumberOfPages;
import static com.mirakl.hybris.core.util.PaginationUtils.getPage;
import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.product.synchro.MiraklProductSynchroTracking;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.catalog.product.MiraklProductSynchroRequest;
import com.mirakl.hybris.core.catalog.strategies.MiraklExportHeaderResolverStrategy;
import com.mirakl.hybris.core.enums.MiraklProductExportHeader;
import com.mirakl.hybris.core.jobs.services.ExportJobReportService;
import com.mirakl.hybris.core.product.strategies.MciProductExportStrategy;
import com.mirakl.hybris.core.util.services.CsvService;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;

public class DefaultMciProductExportStrategy implements MciProductExportStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultMciProductExportStrategy.class);

  protected CsvService csvService;
  protected CategoryService categoryService;
  protected SessionService sessionService;
  protected BaseSiteService baseSiteService;
  protected ExportJobReportService exportJobReportService;
  protected ConfigurationService configurationService;
  protected MiraklMarketplacePlatformFrontApi mmpFrontApi;
  protected Converter<ProductModel, Map<String, String>> productExportConverter;
  protected MiraklExportHeaderResolverStrategy miraklExportHeaderResolverStrategy;

  @Override
  public int exportProducts(Collection<ProductModel> productsToExport, CategoryModel rootCategory,
      CategoryModel rootBrandCategory, BaseSiteModel baseSite, String fileName) throws IOException {
    if (CollectionUtils.isEmpty(productsToExport)) {
      LOG.info("No products to export.");
      return 0;
    }
    List<Map<String, String>> products = convertProducts(productsToExport, rootCategory, rootBrandCategory, baseSite);
    LOG.info(format("Exporting [%d] products", products.size()));

    return exportPaginated(products, fileName);
  }

  protected List<Map<String, String>> convertProducts(final Collection<ProductModel> productsToExport, CategoryModel rootCategory,
      CategoryModel rootBrandCategory, final BaseSiteModel baseSite) {
    HashMap<String, Object> paramMap = new HashMap<>();
    paramMap.put(ALL_BRANDS_CONTEXT_VARIABLE, categoryService.getAllSubcategoriesForCategory(rootBrandCategory));
    paramMap.put(ALL_CATEGORIES_CONTEXT_VARIABLE, categoryService.getAllSubcategoriesForCategory(rootCategory));

    return sessionService.executeInLocalViewWithParams(paramMap, new SessionExecutionBody() {
      @Override
      public Object execute() {
        baseSiteService.setCurrentBaseSite(baseSite, false);
        return productExportConverter.convertAllIgnoreExceptions(productsToExport);
      }
    });
  }

  protected int exportPaginated(List<Map<String, String>> products, String fileName) throws IOException {
    int maximumLinesPerFile = getMaximumLinesPerFile();
    int neededFiles = getNumberOfPages(products.size(), maximumLinesPerFile);
    if (neededFiles > 1 && LOG.isDebugEnabled()) {
      LOG.debug(
          format("Splitting the export into [%s] files. Maximum line number per file is [%s]", neededFiles, maximumLinesPerFile));
    }

    for (int i = 0; i < neededFiles; i++) {
      exportPage(products, i, maximumLinesPerFile, fileName);
    }
    return products.size();
  }

  protected void exportPage(List<Map<String, String>> products, int page, int maxPageSize, String fileName) throws IOException {
    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Exporting page [%s]", page));
    }

    String fileContent =
        csvService.createCsvWithHeaders(miraklExportHeaderResolverStrategy.getSupportedHeaders(MiraklProductExportHeader.class),
            getPage(page, maxPageSize, products));
    MiraklProductSynchroTracking miraklProductSynchroTracking = mmpFrontApi
        .synchronizeProducts(new MiraklProductSynchroRequest(new ByteArrayInputStream(fileContent.getBytes()), fileName));

    exportJobReportService.createMiraklJobReport(miraklProductSynchroTracking.getSynchroId(), PRODUCT_EXPORT);
  }

  protected int getMaximumLinesPerFile() {
    return configurationService.getConfiguration().getInt(PRODUCTS_EXPORT_FILE_MAX_LINE_COUNT);
  }

  @Required
  public void setCsvService(CsvService csvService) {
    this.csvService = csvService;
  }

  @Required
  public void setCategoryService(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @Required
  public void setSessionService(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Required
  public void setBaseSiteService(BaseSiteService baseSiteService) {
    this.baseSiteService = baseSiteService;
  }

  @Required
  public void setExportJobReportService(ExportJobReportService exportJobReportService) {
    this.exportJobReportService = exportJobReportService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setMmpFrontApi(MiraklMarketplacePlatformFrontApi mmpFrontApi) {
    this.mmpFrontApi = mmpFrontApi;
  }

  @Required
  public void setProductExportConverter(Converter<ProductModel, Map<String, String>> productExportConverter) {
    this.productExportConverter = productExportConverter;
  }

  @Required
  public void setMiraklExportHeaderResolverStrategy(MiraklExportHeaderResolverStrategy miraklExportHeaderResolverStrategy) {
    this.miraklExportHeaderResolverStrategy = miraklExportHeaderResolverStrategy;
  }
}
