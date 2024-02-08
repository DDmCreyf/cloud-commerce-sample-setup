package com.mirakl.hybris.occ.controllers;

import com.mirakl.hybris.facades.order.ShippingFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.order.CartWsDTO;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import static de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper.DEFAULT_LEVEL;


/**
 * Web Services Controller to expose the functionality of the {@link ShippingFacade} and the {@link CartFacade} .
 */
@Controller(value = "miraklExtendedCartsController")
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@ApiVersion("v2")
@OpenAPIDefinition(tags =@Tag(name = "Mirakl - Extended Carts"))
@Tag(name = "Mirakl - Extended Carts")
public class MiraklExtendedCartsController extends MiraklBaseController {

  @Resource(name = "shippingFacade")
  private ShippingFacade shippingFacade;
  @Resource(name = "cartFacade")
  private CartFacade cartFacade;

  @RequestMapping(value = "/{cartId}/marketplacedeliverymodes", method = RequestMethod.GET)
  @ResponseBody
  @Operation(operationId = "updateMarketplaceDeliveryModes", summary = "Get cart with marketplace delivery options.",
      description = "Returns the cart with updated marketplace delivery modes.")
  @ApiBaseSiteIdUserIdAndCartIdParam
  public CartWsDTO updateMarketplaceDeliveryModes(
      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_LEVEL) final String fields) {
    shippingFacade.updateAvailableShippingOptions();
    return getDataMapper().map(cartFacade.getSessionCart(), CartWsDTO.class, fields);
  }

  @RequestMapping(value = "/{cartId}/marketplacedeliverymodes", method = RequestMethod.PUT)
  @ResponseBody
  @Operation(operationId = "setMarketplaceDeliveryOption",
      summary = "Sets the marketplace delivery option for the given leadtime to ship and shop.",
      description = "Returns the cart with updated marketplace delivery modes.")
  @ApiBaseSiteIdUserIdAndCartIdParam
  public CartWsDTO setMarketplaceDeliveryOption(
      @Parameter(description = "The selected shipping option code",
          required = true) @RequestParam final String selectedShippingOptionCode,
      @Parameter(description = "The leadtime to ship", required = true) @RequestParam final Integer leadTimeToShip,
      @Parameter(description = "The shop identifier", required = true) @RequestParam final String shopId,
      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_LEVEL) final String fields) throws CommerceCartModificationException {
    shippingFacade.updateShippingOptions(selectedShippingOptionCode, leadTimeToShip, shopId);
    // Using updateAvailableShippingOptions() to recalculate order totals
    shippingFacade.updateAvailableShippingOptions();
    return getDataMapper().map(cartFacade.getSessionCart(), CartWsDTO.class, fields);
  }
}
