package com.mirakl.hybris.occ.controllers;

import com.mirakl.hybris.dto.evaluation.EvaluationPageWsDTO;
import com.mirakl.hybris.dto.shop.ShopWsDTO;
import com.mirakl.hybris.facades.shop.ShopFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Web Services Controller to expose the functionality of the {@link ShopFacade}.
 */
@Controller(value = "miraklShopsController")
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/shops")
@OpenAPIDefinition(tags =@Tag(name = "Mirakl - Shops"))
@Tag(name ="Mirakl - Shops" )
public class MiraklShopsController extends MiraklBaseController {

  @Resource(name = "shopFacade")
  private ShopFacade shopFacade;

  @RequestMapping(value = "/{shopId}", method = RequestMethod.GET)
  @ResponseBody
  @Operation(operationId = "getShop", summary = "Get a shop by identifier", description = "Returns a shop.")
  @ApiBaseSiteIdParam
  public ShopWsDTO getShop(@Parameter(description = "The shop identifier", required = true) @PathVariable final String shopId,
      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletResponse response) {
    return getDataMapper().map(shopFacade.getShopForId(shopId), ShopWsDTO.class, fields);
  }

  @RequestMapping(value = "/{shopId}/evaluations", method = RequestMethod.GET)
  @ResponseBody
  @Operation(operationId = "getShopEvaluations", summary = "Get the shop evaluations by shop identifier",
      description = "Returns the evaluations of a shop.")
  @ApiBaseSiteIdParam
  public EvaluationPageWsDTO getShopEvaluations(
      @Parameter(description = "The shop identifier", required = true) @PathVariable final String shopId,
      @Parameter(description = "The current result page requested", required = true) @RequestParam final Integer currentPage,
      @Parameter(description = "The number of results returned per page", required = true) @RequestParam final Integer pageSize,
      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletResponse response) {
    return getDataMapper().map(shopFacade.getShopEvaluationPage(shopId, getPageableData(currentPage, pageSize)),
        EvaluationPageWsDTO.class, fields);
  }

}
