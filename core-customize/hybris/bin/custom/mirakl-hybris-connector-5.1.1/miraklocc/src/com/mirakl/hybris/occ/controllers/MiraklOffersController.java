package com.mirakl.hybris.occ.controllers;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.beans.OfferDataList;
import com.mirakl.hybris.beans.ProductWithOffers;
import com.mirakl.hybris.beans.ProductsOffers;
import com.mirakl.hybris.core.util.PaginationUtils;
import com.mirakl.hybris.dto.offer.OfferListWsDTO;
import com.mirakl.hybris.dto.offer.ProductsWithOffersListWsDTO;
import com.mirakl.hybris.facades.product.OfferFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Web Services Controller to expose the functionality of the {@link OfferFacade}.
 */
@Controller(value = "miraklOffersController")
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/offers")
@OpenAPIDefinition(tags = @Tag(name = "Mirakl - Offers"))
@Tag(name = "Mirakl - Offers")
public class MiraklOffersController extends MiraklBaseController {

    @Resource(name = "offerFacade")
    private OfferFacade offerFacade;


    @GetMapping
    @ResponseBody
    @Operation(operationId = "getOffers", summary = "Get a list of offers for a given product and additional data",
        description = "Returns a list of offers for a given product and additional data, such as pagination options.")
    @ApiBaseSiteIdParam
    public OfferListWsDTO getOffers(
        @Parameter(description = "The product code used to search offers", required = true) @RequestParam final String productCode,
        @Parameter(description = "The current result page requested. Ignored if the pageSize attribute is empty") @RequestParam(
            required = false) final Integer currentPage,
        @Parameter(description = "The number of results returned per page") @RequestParam(required = false) final Integer pageSize,
        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletResponse response) {
        final OfferDataList offerDataList = new OfferDataList();
        final List<OfferData> offersForProductCode = offerFacade.getOffersForProductCode(productCode);
        if (currentPage != null && pageSize != null && currentPage >= 0 && pageSize > 0) {
            offerDataList.setOffers(PaginationUtils.getSafePage(currentPage, pageSize, offersForProductCode));
        } else {
            offerDataList.setOffers(offersForProductCode);
        }
        return getDataMapper().map(offerDataList, OfferListWsDTO.class, fields);
    }


    @GetMapping(value = "/multi-product")
    @ResponseBody
    @Operation(operationId = "getOffers", summary = "Get a list of offers for multiple products and additional data",
            description = "Returns a list of offers for multiple products and additional data, such as pagination options.")
    @ApiBaseSiteIdParam
    public ProductsWithOffersListWsDTO getOffers(
            @Parameter(description = "The list of product codes used to search for offers", required = true, explode = Explode.FALSE, array = @ArraySchema(schema = @Schema(type = "string"))) @RequestParam final List<String> productCodes,
            @Parameter(description = "The current result page requested. Ignored if the pageSize attribute is empty") @RequestParam(required = false) final Integer currentPage,
            @Parameter(description = "The number of results returned per page") @RequestParam(required = false) final Integer pageSize,
            @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletResponse response) {
        final Map<String, List<OfferData>> offersForProductCode = offerFacade.getOffersForProductCodes(productCodes);
        ProductsOffers productsWithOffers = new ProductsOffers();
        productsWithOffers.setProducts(new ArrayList<>());
        for (Map.Entry<String, List<OfferData>> productOffers : offersForProductCode.entrySet()) {
            List<OfferData> offers;
            if (currentPage != null && pageSize != null && currentPage >= 0 && pageSize > 0) {
                offers = PaginationUtils.getSafePage(currentPage, pageSize, productOffers.getValue());
            } else {
                offers = productOffers.getValue();
            }
            ProductWithOffers productWithOffers = new ProductWithOffers();
            productWithOffers.setCode(productOffers.getKey());
            productWithOffers.setOffers(offers);
            productsWithOffers.getProducts().add(productWithOffers);
        }

        return getDataMapper().map(productsWithOffers, ProductsWithOffersListWsDTO.class, fields);
    }
}
