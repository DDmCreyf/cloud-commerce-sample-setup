package com.mirakl.hybris.core.constants;

import static java.lang.String.format;

/**
 * Global class for all Miraklservices constants. You can add global constants for your extension into this class.
 */
public final class MiraklservicesConstants extends GeneratedMiraklservicesConstants {
  public static final String EXTENSIONNAME = "miraklservices";
  public static final String REFUNDS_CONFIRMATION_BATCHSIZE_PROPERTY_KEY = "mirakl.refunds.confirmation.batchsize";

  private MiraklservicesConstants() {
    // empty to avoid instantiating this constant class
  }

  public static final String MIRAKL_ENV_FRONTAPIKEY = "mirakl.env.frontapikey";
  public static final String MIRAKL_ENV_OPERATORAPIKEY = "mirakl.env.operatorapikey";
  public static final String MIRAKL_ENV_URL = "mirakl.env.url";
  public static final String MIRAKL_CUSTOM_USER_AGENT_ENABLED = "mirakl.api.customuseragent.enabled";
  public static final String MIRAKL_CUSTOM_USER_AGENT_VALUE = "mirakl.api.customuseragent.value";
  public static final String MEDIA_URL_SECURE = "media.url.secure";
  public static final String OFFER_NEW_STATE_CODE_KEY = "mirakl.offers.state.new.code";
  public static final String UPDATE_RECEIVED_EVENT_NAME = "_UpdateReceived";
  public static final String BOOLEAN_VALUE_LIST_ID = "boolean-values";
  public static final String BOOLEAN_TYPE_PARAMETERS = format("LIST_CODE|%s", BOOLEAN_VALUE_LIST_ID);
  public static final String PRODUCTS_IMPORT_MAX_DURATION = "mirakl.products.import.maximumduration";
  public static final String PRODUCTS_IMPORT_VALUES_SEPARATOR = "mirakl.products.import.valuesseparator";
  public static final String PRODUCTS_IMPORT_LOCALIZED_ATTRIBUTE_REGEX = "mirakl.products.import.localizedattributeregex";
  public static final String PRODUCTS_IMPORT_RESULT_QUEUE_LENGTH = "mirakl.products.import.resultqueuelength";
  public static final String PRODUCTS_IMPORT_ADDITIONAL_MESSAGE_HEADER = "mirakl.products.import.additionalmessageheader";
  public static final String PRODUCTS_IMPORT_ERROR_MESSAGE_HEADER = "mirakl.products.import.errormessageheader";
  public static final String PRODUCTS_IMPORT_ERROR_LINE_HEADER = "mirakl.products.import.errorlineheader";
  public static final String PRODUCTS_IMPORT_ORIGINAL_LINE_NUMBER_HEADER = "mirakl.products.import.originallinenumberheader";
  public static final String PRODUCTS_IMPORT_ERROR_FILENAME_SUFFIX = "mirakl.products.import.errorfilenamesuffix";
  public static final String PRODUCTS_IMPORT_SUCCESS_FILENAME_SUFFIX = "mirakl.products.import.successfilenamesuffix";
  public static final String PRODUCTS_IMPORT_IMAGES_FOLDER = "mirakl.products.import.imagefolder";
  public static final String PRODUCTS_IMPORT_ALREADY_RECEIVED_MESSAGE = "text.products.import.alreadyreceived";
  public static final String PRODUCTS_IMPORT_MEDIA_DOWNLOAD_FAILURE_MESSAGE = "text.products.import.mediadownloadfailure";
  public static final String CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_VALUE = "mirakl.catalog.export.typeparameters.decimal.defaultvalue";
  public static final String CATALOG_EXPORT_MEDIA_TYPE_PARAMETERS_DEFAULT_VALUE = "mirakl.catalog.export.typeparameters.media.defaultvalue";
  public static final String CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_VALUE = "mirakl.catalog.export.typeparameters.date.defaultvalue";
  public static final String CATALOG_EXPORT_DATE_FORMAT = "mirakl.catalog.export.date.format";
  public static final String CATALOG_EXPORT_DATE_TYPE_PARAMETERS_DEFAULT_PATTERN = "PATTERN|%s";
  public static final String CATALOG_EXPORT_DECIMAL_TYPE_PARAMETERS_DEFAULT_PATTERN = "PRECISION|%s";
  public static final String CATALOG_EXPORT_MEDIA_TYPE_PARAMETERS_DEFAULT_PATTERN = "MAX_SIZE|%s";
  public static final String CATALOG_EXPORT_LIST_TYPE_PARAMETERS_DEFAULT_PATTERN = "LIST_CODE|%s";
  public static final String CATALOG_EXPORT_LOCALIZED_ATTRIBUTE_PATTERN = "mirakl.catalog.export.localizedattributepattern";
  public static final String CATALOG_EXPORT_DEFAULT_ENCODING = "UTF-8";
  public static final String CATALOG_EXPORT_FILE_EXTENSION = ".csv";
  public static final String LOCALE_MAPPING_MIRAKL_TO_HYBRIS_PREFIX = "mirakl.locales.mapping.mirakltohybris";
  public static final String LOCALE_MAPPING_HYBRIS_TO_MIRAKL_PREFIX = "mirakl.locales.mapping.hybristomirakl";
  public static final String ERROR_REPORT_MIME_TYPE = "text/csv";
  public static final String USER_AGENT_HYBRIS_VERSION_PLACEHOLDER = "HYBRIS_VERSION";
  public static final String USER_AGENT_MIRAKL_CONNECTOR_VERSION_PLACEHOLDER = "MIRAKL_CONNECTOR_VERSION";
  public static final String USER_AGENT_DEFAULT_USER_AGENT_PLACEHOLDER = "DEFAULT_USER_AGENT";
  public static final String USER_AGENT_HEADER = "User-Agent";
  public static final String ENABLE_PAYMENT_REQUEST_PULLING = "mirakl.payment.enablerequestpulling";
  public static final String MAX_DEBIT_ORDERS_PAGE_SIZE = "mirakl.payment.debit.orders.pagesize";
  public static final String MAX_REFUND_ORDERS_PAGE_SIZE = "mirakl.payment.refund.orders.pagesize";
  public static final String INBOX_MESSAGES_PAGE_SIZE = "mirakl.inbox.messages.pagesize";
  public static final String SOCKET_CONFIG_SO_KEEPALIVE = "mirakl.httpclient.socketconfig.sokeepalive";
  public static final String SOCKET_CONFIG_SO_TIMEOUT = "mirakl.httpclient.socketconfig.sotimeout";
  public static final String REQUEST_CONFIG_SOCKET_TIMEOUT = "mirakl.httpclient.requestconfig.sockettimeout";
  public static final String REQUEST_CONFIG_CONNECT_TIMEOUT = "mirakl.httpclient.requestconfig.connecttimeout";
  public static final String CLIENT_DROP_IDLE_CONNECTIONS = "mirakl.httpclient.dropidleconnections";
  public static final String CLIENT_KEEP_ALIVE_DURATION = "mirakl.httpclient.clientkeepaliveduration";
  public static final String USE_ASYNC_OFFER_IMPORT = "mirakl.offers.import.usemiraklasynctask";
  public static final String REALTIME_OFFER_PRICE_ACCESS_MODE = "mirakl.realtime.offer.price.access.mode";
  public static final String REALTIME_OFFER_PRICE_FETCH_PAGESIZE = "mirakl.realtime.offer.price.fetch.pagesize";


}
