<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shipping" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/checkout/multi" %>
<%@ attribute name="deliveryGroup" required="true" type="de.hybris.platform.commercefacades.order.data.DeliveryOrderEntryGroupData" %>

<c:if test="${not empty deliveryGroup.entries}">
	<div class="row">
	    <div class="col-sm-12">
	        <div class="checkout-shipping-items-header">
	           <spring:theme code="checkout.summary.deliveryMode.soldBy" text="Sold By"/>
	           &nbsp;${deliveryGroup.shopName}&nbsp;-&nbsp;${deliveryGroup.quantity}&nbsp;item(s)
	        </div>
	    </div>
	    <div class="col-sm-6">
	        <shipping:groupItems
	                deliveryGroupEntries="${deliveryGroup.entries}"/>
	    </div>
	    <div class="col-sm-6">
	        <shipping:shippingOptionSelector
	                shippingOptions="${deliveryGroup.availableShippingOptions}"
	                selectedShippingOptionCode="${deliveryGroup.selectedShippingOption.code}"/>
	    </div>
	
	    <c:if test="${deliveryGroup.leadTimeToShip gt 0}">
	        <div class="col-sm-6 col-sm-offset-6">
	            <spring:theme
	                    code="checkout.summary.deliveryMode.group.leadTimeToShip"
	                    text="Lead time to ship"/>:&nbsp;${deliveryGroup.leadTimeToShip}&nbsp;days
	        </div>
	    </c:if>
	    <div class="col-sm-6 col-sm-offset-6">
	        <spring:theme
	                code="checkout.summary.deliveryMode.group.shippingCost"
	                text="Shipping cost"/>:&nbsp;${deliveryGroup.selectedShippingOption.deliveryCost.formattedValue}
	    </div>
	</div>
</c:if>
