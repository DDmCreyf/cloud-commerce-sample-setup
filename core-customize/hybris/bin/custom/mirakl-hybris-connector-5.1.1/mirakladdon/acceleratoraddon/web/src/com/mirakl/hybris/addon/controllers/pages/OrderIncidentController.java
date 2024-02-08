package com.mirakl.hybris.addon.controllers.pages;

import static com.mirakl.client.mmp.domain.reason.MiraklReasonType.INCIDENT_CLOSE;
import static com.mirakl.client.mmp.domain.reason.MiraklReasonType.INCIDENT_OPEN;
import static com.mirakl.hybris.addon.utils.InboxUtils.handleHtmlBreakingLines;
import static java.lang.String.format;
import static java.util.Collections.singleton;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.hybris.addon.controllers.MirakladdonControllerConstants;
import com.mirakl.hybris.addon.forms.IncidentForm;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ThreadRecipientData;
import com.mirakl.hybris.core.enums.MiraklThreadParticipantType;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.facades.order.IncidentFacade;
import com.mirakl.hybris.facades.order.MarketplaceConsignmentFacade;
import com.mirakl.hybris.facades.setting.ReasonFacade;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.addonsupport.controllers.page.AbstractAddOnPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

@Controller
@RequestMapping(value = "/my-account/consignment")
public class OrderIncidentController extends AbstractAddOnPageController {

  private static final Logger LOG = Logger.getLogger(OrderIncidentController.class);

  protected static final String OPEN_INCIDENT_URL = "{consignmentCode:.*}/open-incident/{consignmentEntryCode:.*}";
  protected static final String CLOSE_INCIDENT_URL = "{consignmentCode:.*}/close-incident/{consignmentEntryCode:.*}";
  protected static final String REDIRECT_TO_ORDER_DETAIL_PAGE = REDIRECT_PREFIX + "/my-account/order/";

  protected UserService userService;
  protected IncidentFacade incidentFacade;
  protected ReasonFacade reasonFacade;
  protected MarketplaceConsignmentService marketplaceConsignmentService;
  protected MarketplaceConsignmentFacade marketplaceConsignmentFacade;

  @RequireHardLogIn
  @RequestMapping(value = OPEN_INCIDENT_URL, method = RequestMethod.GET)
  public String openIncidentPage(@PathVariable("consignmentEntryCode") final String consignmentEntryCode, final Model model,
      final HttpServletRequest request, final HttpServletResponse response)
          throws CMSItemNotFoundException, UnsupportedEncodingException {

    model.addAttribute("reasons", reasonFacade.getReasons(INCIDENT_OPEN));
    model.addAttribute("product", marketplaceConsignmentFacade.getProductForConsignmentEntry(consignmentEntryCode));

    return MirakladdonControllerConstants.Fragments.Order.orderIncidentPopup;
  }

  @RequireHardLogIn
  @RequestMapping(value = OPEN_INCIDENT_URL, method = RequestMethod.POST)
  public String postOpenIncident(@PathVariable("consignmentCode") final String consignmentCode,
      @PathVariable("consignmentEntryCode") final String consignmentEntryCode, final Model model,
      final HttpServletRequest request, @Valid final IncidentForm form, final HttpServletResponse response,
      final RedirectAttributes redirectModel) {

    AbstractOrderModel order = marketplaceConsignmentService.getMarketplaceConsignmentForCode(consignmentCode).getOrder();

    try {
      incidentFacade.openIncident(consignmentEntryCode, form.getReasonCode(), getMessageThreadData(form, INCIDENT_OPEN));
      GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER,
          "consignmentEntry.incident.open.success");
    } catch (Exception e) {
      GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "consignmentEntry.incident.open.error");
      LOG.error(e);
    }

    return REDIRECT_TO_ORDER_DETAIL_PAGE + order.getCode();
  }

  @RequireHardLogIn
  @RequestMapping(value = CLOSE_INCIDENT_URL, method = RequestMethod.GET)
  public String closeIncidentPage(@PathVariable("consignmentEntryCode") final String consignmentEntryCode, final Model model,
      final HttpServletRequest request, final HttpServletResponse response)
          throws CMSItemNotFoundException, UnsupportedEncodingException {

    model.addAttribute("reasons", reasonFacade.getReasons(INCIDENT_CLOSE));
    model.addAttribute("product", marketplaceConsignmentFacade.getProductForConsignmentEntry(consignmentEntryCode));

    return MirakladdonControllerConstants.Fragments.Order.orderIncidentPopup;
  }

  @RequireHardLogIn
  @RequestMapping(value = CLOSE_INCIDENT_URL, method = RequestMethod.POST)
  public String postCloseIncident(@PathVariable("consignmentCode") final String consignmentCode,
      @PathVariable("consignmentEntryCode") final String consignmentEntryCode, final Model model,
      final HttpServletRequest request, @Valid final IncidentForm form, final HttpServletResponse response,
      final RedirectAttributes redirectModel) {

    AbstractOrderModel order = marketplaceConsignmentService.getMarketplaceConsignmentForCode(consignmentCode).getOrder();

    try {
      incidentFacade.closeIncident(consignmentEntryCode, form.getReasonCode(), getMessageThreadData(form, INCIDENT_CLOSE));
      GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER,
          "consignmentEntry.incident.close.success");
    } catch (Exception e) {
      GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
          "consignmentEntry.incident.close.error");
      LOG.error(e);
    }

    return REDIRECT_TO_ORDER_DETAIL_PAGE + order.getCode();
  }

  protected CreateThreadMessageData getMessageThreadData(@Valid IncidentForm form, MiraklReasonType reasonType) {
    CreateThreadMessageData messageThreadData = new CreateThreadMessageData();
    messageThreadData.setBody(handleHtmlBreakingLines(form.getMessage()));
    messageThreadData.setTo(getThreadRecipients());
    messageThreadData.setTopic(getTopic(form, reasonType));
    return messageThreadData;
  }

  protected String getTopic(IncidentForm form, MiraklReasonType reasonType) {
    Map<String, String> reasons = reasonFacade.getReasonsAsMap(reasonType);
    if (!reasons.containsKey(form.getReasonCode())) {
      throw new IllegalArgumentException(format("Unknown incident reason for code [%s]", form.getReasonCode()));
    }
    return reasons.get(form.getReasonCode());
  }

  protected Set<ThreadRecipientData> getThreadRecipients() {
    ThreadRecipientData shopRecipient = new ThreadRecipientData();
    shopRecipient.setType(MiraklThreadParticipantType.SHOP.getCode());
    return singleton(shopRecipient);
  }

  @ExceptionHandler(UnknownIdentifierException.class)
  public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request) {
    request.setAttribute("message", exception.getMessage());
    return FORWARD_PREFIX + "/404";
  }

  @Required
  public void setReasonFacade(ReasonFacade reasonFacade) {
    this.reasonFacade = reasonFacade;
  }

  @Required
  public void setIncidentFacade(IncidentFacade incidentFacade) {
    this.incidentFacade = incidentFacade;
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

  @Required
  public void setMarketplaceConsignmentFacade(MarketplaceConsignmentFacade marketplaceConsignmentFacade) {
    this.marketplaceConsignmentFacade = marketplaceConsignmentFacade;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }
}
