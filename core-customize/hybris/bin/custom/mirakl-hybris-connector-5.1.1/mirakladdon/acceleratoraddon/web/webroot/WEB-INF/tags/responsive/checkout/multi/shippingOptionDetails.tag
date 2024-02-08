<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="shippingOption" required="true" type="de.hybris.platform.commercefacades.order.data.DeliveryModeData" %>
<%@ attribute name="isSelected" required="false" type="java.lang.Boolean" %>

<option value="${shippingOption.code}" ${isSelected ? 'selected="selected"' : ''}>
	${shippingOption.name}
</option>