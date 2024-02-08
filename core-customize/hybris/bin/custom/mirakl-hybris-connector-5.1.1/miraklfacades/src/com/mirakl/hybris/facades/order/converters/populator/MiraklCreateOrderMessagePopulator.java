package com.mirakl.hybris.facades.order.converters.populator;

import static com.mirakl.client.core.internal.util.Preconditions.checkArgument;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderMessage;
import com.mirakl.hybris.beans.MessageData;

import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;

public class MiraklCreateOrderMessagePopulator implements Populator<MessageData, MiraklCreateOrderMessage> {

  protected UserService userService;
  protected CustomerNameStrategy customerNameStrategy;

  @Override
  public void populate(MessageData source, MiraklCreateOrderMessage target) throws ConversionException {
    validateParameterNotNullStandardMessage("source", source);
    validateParameterNotNullStandardMessage("target", target);

    CustomerModel customer = (CustomerModel) userService.getUserForUID(source.getAuthorId());
    validateParameterNotNullStandardMessage("customer", customer);

    target.setBody(source.getBody());
    target.setSubject(source.getSubject());
    target.setCustomerEmail(customer.getContactEmail());
    target.setCustomerId(customer.getUid());
    target.setToCustomer(false);
    target.setToOperator(false);
    target.setToShop(true);
    setCustomerName(target, customer);
  }

  protected void setCustomerName(MiraklCreateOrderMessage miraklCreateOrderMessage, CustomerModel customer) {
    String[] customerName = customerNameStrategy.splitName(customer.getName());

    checkArgument(isNotBlank(customerName[0]), format("No first name found for customer with uid [%s]", customer.getUid()));
    checkArgument(isNotBlank(customerName[1]), format("No last name found for customer with uid [%s]", customer.getUid()));

    miraklCreateOrderMessage.setCustomerFirstname(customerName[0]);
    miraklCreateOrderMessage.setCustomerLastname(customerName[1]);
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Required
  public void setCustomerNameStrategy(CustomerNameStrategy customerNameStrategy) {
    this.customerNameStrategy = customerNameStrategy;
  }
}
