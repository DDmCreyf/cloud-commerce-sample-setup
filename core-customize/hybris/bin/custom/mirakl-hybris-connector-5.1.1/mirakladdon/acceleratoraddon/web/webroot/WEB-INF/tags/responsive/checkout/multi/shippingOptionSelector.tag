<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="shippingOptions" required="true" type="java.util.List" %>
<%@ attribute name="selectedShippingOptionCode" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shipping" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/checkout/multi" %>

<select id="shipping_option" name="shipping_option" class="form-control" onchange="submit()">
	<c:forEach items="${shippingOptions}" var="shippingOption">
		<shipping:shippingOptionDetails shippingOption="${shippingOption}" isSelected="${shippingOption.code eq selectedShippingOptionCode}"/>
	</c:forEach>
</select>
