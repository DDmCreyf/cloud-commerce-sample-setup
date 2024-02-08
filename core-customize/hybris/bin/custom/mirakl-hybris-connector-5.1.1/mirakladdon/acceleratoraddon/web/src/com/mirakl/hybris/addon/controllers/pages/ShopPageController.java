package com.mirakl.hybris.addon.controllers.pages;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang.StringUtils.isNumeric;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mirakl.hybris.addon.controllers.MirakladdonControllerConstants;
import com.mirakl.hybris.beans.EvaluationPageData;
import com.mirakl.hybris.beans.ShopData;
import com.mirakl.hybris.facades.shop.ShopFacade;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.addonsupport.controllers.page.AbstractAddOnPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

@Controller
@RequestMapping(value = "/**/sellers")
public class ShopPageController extends AbstractAddOnPageController {

  public static final String SHOP_CODE_PATH_VARIABLE_PATTERN = "/{shopId:.*}";
  public static final String SHOP_EVALUATION_PAGE_VARIABLE_PATTERN = "/{evaluationPage:.*}";
  public static final String SHOP_ATTRIBUTE = "shop";
  public static final String CURRENT_EVALUATION_PAGE = "currentPage";
  public static final String EVALUATION_PAGE_CONTENT = "evaluationPageContent";

  protected CMSPageService cmsPageService;
  protected ShopFacade shopFacade;
  protected ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;
  protected int evaluationPageSize;


  @RequestMapping(value = SHOP_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
  public String shopDetail(@PathVariable("shopId") final String shopId, final Model model, final HttpServletRequest request,
      final HttpServletResponse response) throws CMSItemNotFoundException, UnsupportedEncodingException {

    ShopData shop = shopFacade.getShopForId(shopId);

    model.addAttribute(WebConstants.BREADCRUMBS_KEY, getBreadcrumbs(shop));
    model.addAttribute(SHOP_ATTRIBUTE, shop);

    storeCmsPageInModel(model, cmsPageService.getPageForLabelOrId("shop-information"));
    storeContentPageTitleInModel(model, shop.getName());

    return getViewForPage(model);
  }

  @RequestMapping(value = SHOP_CODE_PATH_VARIABLE_PATTERN + "/reviews" + SHOP_EVALUATION_PAGE_VARIABLE_PATTERN,
      method = RequestMethod.GET)
  public String shopEvaluationsTab(@PathVariable("shopId") final String shopId, @PathVariable("evaluationPage") final String page,
      final Model model, final HttpServletRequest request, final HttpServletResponse response)
          throws CMSItemNotFoundException, UnsupportedEncodingException {

    int pageNumber = isNumeric(page) ? parseInt(page) : 0;
    PageableData pageableData = new PageableData();
    pageableData.setPageSize(evaluationPageSize);
    pageableData.setCurrentPage(pageNumber);
    EvaluationPageData evaluationPage = shopFacade.getShopEvaluationPage(shopId, pageableData);
    model.addAttribute(EVALUATION_PAGE_CONTENT, evaluationPage);
    model.addAttribute(CURRENT_EVALUATION_PAGE, pageNumber);

    return MirakladdonControllerConstants.Fragments.Shop.shopEvaluationFragment;
  }

  @ExceptionHandler(UnknownIdentifierException.class)
  public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request) {
    request.setAttribute("message", exception.getMessage());
    return FORWARD_PREFIX + "/404";
  }

  private List<Breadcrumb> getBreadcrumbs(ShopData shop) {
    List<Breadcrumb> breadcrumbs = new ArrayList<>();
    breadcrumbs.addAll(resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.shopinfo"));
    if (shop != null) {
      breadcrumbs.add(new Breadcrumb("#", shop.getName(), null));
    }
    return breadcrumbs;
  }

  @Required
  public void setCmsPageService(CMSPageService cmsPageService) {
    this.cmsPageService = cmsPageService;
  }

  @Required
  public void setResourceBreadcrumbBuilder(ResourceBreadcrumbBuilder resourceBreadcrumbBuilder) {
    this.resourceBreadcrumbBuilder = resourceBreadcrumbBuilder;
  }

  @Required
  public void setShopFacade(ShopFacade shopFacade) {
    this.shopFacade = shopFacade;
  }

  @Required
  public void setEvaluationPageSize(int evaluationPageSize) {
    this.evaluationPageSize = evaluationPageSize;
  }
}
