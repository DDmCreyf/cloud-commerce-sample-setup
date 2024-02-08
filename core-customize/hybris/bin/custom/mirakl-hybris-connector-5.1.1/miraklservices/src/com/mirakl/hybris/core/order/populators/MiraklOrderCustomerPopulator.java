package com.mirakl.hybris.core.order.populators;

import static com.mirakl.client.core.internal.util.Preconditions.checkArgument;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.order.MiraklCustomerShippingAddress;
import com.mirakl.client.mmp.domain.order.MiraklOrderCustomer;
import com.mirakl.hybris.core.order.strategies.MiraklCustomerIdDefinitionStrategy;
import com.mirakl.hybris.core.util.strategies.LocaleMappingStrategy;

import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklOrderCustomerPopulator implements Populator<OrderModel, MiraklOrderCustomer> {

  private static final Logger LOG = Logger.getLogger(MiraklOrderCustomerPopulator.class);

  protected static final String LOCALE_MIRAKL_PREFIX = "mirakl.locales.mapping.";

  protected CustomerNameStrategy customerNameStrategy;
  protected Converter<AddressModel, MiraklCustomerShippingAddress> miraklCustomerAddressConverter;
  protected LocaleMappingStrategy localeMappingStrategy;
  protected MiraklCustomerIdDefinitionStrategy miraklCustomerIdDefinitionStrategy;

  @Override
  public void populate(OrderModel order, MiraklOrderCustomer miraklOrderCustomer) throws ConversionException {
    validateParameterNotNullStandardMessage("order", order);
    validateParameterNotNullStandardMessage("miraklOrderCustomer", miraklOrderCustomer);

    CustomerModel customer = (CustomerModel) order.getUser();
    validateParameterNotNullStandardMessage("customer", customer);

    miraklOrderCustomer.setEmail(customer.getContactEmail());
    miraklOrderCustomer.setId(miraklCustomerIdDefinitionStrategy.getMiraklCustomerId(customer));

    setCustomerName(miraklOrderCustomer, customer);
    setCustomerLocale(miraklOrderCustomer, order);
    setCustomerAddresses(miraklOrderCustomer, order);
  }

  protected void setCustomerName(MiraklOrderCustomer miraklOrderCustomer, CustomerModel customer) {
    String[] customerName = customerNameStrategy.splitName(customer.getName());

    checkArgument(isNotBlank(customerName[0]), format("No first name found for customer with uid [%s]", customer.getUid()));
    checkArgument(isNotBlank(customerName[1]), format("No last name found for customer with uid [%s]", customer.getUid()));

    miraklOrderCustomer.setFirstname(customerName[0]);
    miraklOrderCustomer.setLastname(customerName[1]);
  }

  protected void setCustomerLocale(MiraklOrderCustomer miraklOrderCustomer, OrderModel order) {
    LanguageModel language = order.getLanguage();
    if (language == null) {
      LOG.warn(format("No language was found on order [%s]. The customer locale will not be populated.", order.getCode()));
      return;
    }
    miraklOrderCustomer.setLocale(localeMappingStrategy.mapToMiraklLocale(language));
  }

  protected void setCustomerAddresses(MiraklOrderCustomer miraklOrderCustomer, OrderModel order) {
    miraklOrderCustomer.setShippingAddress(miraklCustomerAddressConverter.convert(order.getDeliveryAddress()));
    miraklOrderCustomer.setBillingAddress(miraklCustomerAddressConverter.convert(order.getPaymentAddress()));
  }

  @Required
  public void setCustomerNameStrategy(CustomerNameStrategy customerNameStrategy) {
    this.customerNameStrategy = customerNameStrategy;
  }

  @Required
  public void setMiraklCustomerAddressConverter(
      Converter<AddressModel, MiraklCustomerShippingAddress> miraklCustomerAddressConverter) {
    this.miraklCustomerAddressConverter = miraklCustomerAddressConverter;
  }

  @Required
  public void setLocaleMappingStrategy(LocaleMappingStrategy localeMappingStrategy) {
    this.localeMappingStrategy = localeMappingStrategy;
  }

  @Required
  public void setMiraklCustomerIdDefinitionStrategy(MiraklCustomerIdDefinitionStrategy miraklCustomerIdDefinitionStrategy) {
    this.miraklCustomerIdDefinitionStrategy = miraklCustomerIdDefinitionStrategy;
  }
}
