<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shipping" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/checkout/multi" %>
<%@ attribute name="deliveryGroup" required="true" type="de.hybris.platform.commercefacades.order.data.DeliveryOrderEntryGroupData" %>
<%@ attribute name="deliveryMethods" required="true" type="java.util.List" %>
<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<div class="row">
    <div class="col-sm-12">
        <div class="checkout-shipping-items-header">
            <spring:theme code="checkout.summary.deliveryMode.soldBy" text="Sold By"/>
            &nbsp;${cmsSite.operatorName}&nbsp;-&nbsp;${deliveryGroup.quantity}&nbsp;item(s)
        </div>
    </div>
    <div class="col-sm-6">
        <shipping:groupItems
                deliveryGroupEntries="${deliveryGroup.entries}"/>
    </div>
    <div class="col-sm-6">
        <shipping:shippingOptionSelector
                shippingOptions="${deliveryMethods}"
                selectedShippingOptionCode="${cartData.deliveryMode.code}"/>
    </div>
    <div class="col-sm-12 col-sm-offset-6">
        <spring:theme
                code="checkout.summary.deliveryMode.group.shippingCost"
                text="Shipping cost"/>:&nbsp;${cartData.deliveryMode.deliveryCost.formattedValue}
    </div>
</div>
