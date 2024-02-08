package com.mirakl.hybris.occ.controllers;

import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.hybris.beans.ReasonDataList;
import com.mirakl.hybris.dto.setting.ReasonListWsDTO;
import com.mirakl.hybris.facades.setting.ReasonFacade;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
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
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang3.LocaleUtils.toLocale;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Web Services Controller to expose the functionality of the {@link ReasonFacade}.
 */
@Controller(value = "miraklReasonsController")
@ApiVersion("v2")
@RequestMapping(value = "/{baseSiteId}/reasons")
@OpenAPIDefinition(tags =@Tag(name = "Mirakl - Reasons"))
@Tag(name = "Mirakl - Reasons")
public class MiraklReasonsController extends MiraklBaseController {

  @Resource(name = "reasonFacade")
  private ReasonFacade reasonFacade;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseBody
  @Operation(operationId = "getReasons", summary = "Get a list of reasons by type",
      description = "Returns a list refund, incident, and message reasons by type.\n"
          + "Reasons are sorted by sort index, set in Mirakl.")
  @ApiBaseSiteIdParam
  public ReasonListWsDTO getReasons(
      @Parameter(description = "The mirakl reason type", required = true) @RequestParam final MiraklReasonType type,
      @Parameter(description = "The locale in which to retrieve the labels",
          required = false) @RequestParam(required = false) final String locale,
      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletResponse response) {
    final ReasonDataList reasonDataList = new ReasonDataList();
    if (isNotEmpty(locale)) {
      reasonDataList.setReasons(reasonFacade.getReasons(type, toLocale(locale)));
    } else {
      reasonDataList.setReasons(reasonFacade.getReasons(type));
    }
    return getDataMapper().map(reasonDataList, ReasonListWsDTO.class, fields);
  }

}
