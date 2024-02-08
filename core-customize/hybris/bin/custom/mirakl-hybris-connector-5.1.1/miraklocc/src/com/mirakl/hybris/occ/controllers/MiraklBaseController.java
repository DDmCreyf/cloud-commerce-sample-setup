package com.mirakl.hybris.occ.controllers;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;
import com.mirakl.client.core.exception.MiraklApiException;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.util.YSanitizer;

/**
 * Mirakl Base Controller. It defines the exception handler to be used by all controllers. Extending controllers can add or
 * overwrite the exception handler if needed.
 */
@Controller(value = "miraklBaseController")
@ApiVersion("v2")
public class MiraklBaseController {
  protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;
  protected static final String INVALID_REQUEST_BODY_ERROR_MESSAGE = "Request body is invalid or missing";

  private static final Logger LOG = LoggerFactory.getLogger(MiraklBaseController.class);

  @Resource(name = "dataMapper")
  private DataMapper dataMapper;

  protected static String sanitize(final String input) {
    return YSanitizer.sanitize(input);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler({ModelNotFoundException.class})
  public ErrorListWsDTO handleModelNotFoundException(final Exception ex) {
    LOG.info("Handling Exception for this request - {} - {}", ex.getClass().getSimpleName(), sanitize(ex.getMessage()));
    LOG.debug("An exception occurred!", ex);

    return handleErrorInternal(UnknownIdentifierException.class.getSimpleName(), ex.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler({IllegalArgumentException.class})
  public ErrorListWsDTO handleIllegalArgumentException(final Exception ex) {
    LOG.info("Handling Exception for this request - {} - {}", ex.getClass().getSimpleName(), sanitize(ex.getMessage()));
    LOG.debug("An exception occurred!", ex);

    return handleErrorInternal(IllegalArgumentException.class.getSimpleName(), ex.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler({MiraklApiException.class})
  public ErrorListWsDTO handleMiraklApiException(final Exception ex) {
    LOG.info("Handling Exception for this request - {} - {}", ex.getClass().getSimpleName(), sanitize(ex.getMessage()));
    LOG.debug("An exception occurred!", ex);

    return handleErrorInternal(MiraklApiException.class.getSimpleName(), ex.getMessage());
  }

  protected ErrorListWsDTO handleErrorInternal(final String type, final String message) {
    final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
    final ErrorWsDTO error = new ErrorWsDTO();
    error.setType(type.replace("Exception", "Error"));
    error.setMessage(sanitize(message));
    errorListDto.setErrors(Lists.newArrayList(error));
    return errorListDto;
  }

  protected void validate(final Object object, final String objectName, final Validator validator) {
    final Errors errors = new BeanPropertyBindingResult(object, objectName);
    validator.validate(object, errors);
    if (errors.hasErrors()) {
      throw new WebserviceValidationException(errors);
    }
  }

  protected static PageableData getPageableData(Integer currentPage, Integer pageSize) {
    final PageableData pageableData = new PageableData();
    pageableData.setCurrentPage(currentPage);
    pageableData.setPageSize(pageSize);
    return pageableData;
  }

  protected DataMapper getDataMapper() {
    return dataMapper;
  }

  protected void setDataMapper(final DataMapper dataMapper) {
    this.dataMapper = dataMapper;
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler({DuplicateUidException.class})
  public ErrorListWsDTO handleDuplicateUidException(final DuplicateUidException ex) {
    LOG.debug("DuplicateUidException", ex);
    return handleErrorInternal("DuplicateUidException", ex.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler({HttpMessageNotReadableException.class})
  public ErrorListWsDTO handleHttpMessageNotReadableException(final Exception ex) {
    LOG.debug(INVALID_REQUEST_BODY_ERROR_MESSAGE, ex);
    return handleErrorInternal(HttpMessageNotReadableException.class.getSimpleName(), INVALID_REQUEST_BODY_ERROR_MESSAGE);
  }

}
