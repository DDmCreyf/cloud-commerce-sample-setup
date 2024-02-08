<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="deliveryAddress" required="true" type="de.hybris.platform.commercefacades.user.data.AddressData" %>

<div class="checkout-shipping-items row">

    <c:if test="${not empty deliveryAddress}">
        <div class="col-sm-12 col-lg-6">
            <div class="checkout-shipping-items-header"><spring:theme code="checkout.summary.shippingAddress"/></div>
                <span>
                    <b>${fn:escapeXml(deliveryAddress.title)}&nbsp;${fn:escapeXml(deliveryAddress.firstName)}&nbsp;${fn:escapeXml(deliveryAddress.lastName)}</b>
                    <br/>
                    <c:if test="${ not empty deliveryAddress.line1 }">
                        ${fn:escapeXml(deliveryAddress.line1)},&nbsp;
                    </c:if>
                    <c:if test="${ not empty deliveryAddress.line2 }">
                        ${fn:escapeXml(deliveryAddress.line2)},&nbsp;
                    </c:if>
                    <c:if test="${not empty deliveryAddress.town }">
                        ${fn:escapeXml(deliveryAddress.town)},&nbsp;
                    </c:if>
                    <c:if test="${ not empty deliveryAddress.region.name }">
                        ${fn:escapeXml(deliveryAddress.region.name)},&nbsp;
                    </c:if>
                    <c:if test="${ not empty deliveryAddress.postalCode }">
                        ${fn:escapeXml(deliveryAddress.postalCode)},&nbsp;
                    </c:if>
                    <c:if test="${ not empty deliveryAddress.country.name }">
                        ${fn:escapeXml(deliveryAddress.country.name)}
                    </c:if>
                    <br/>
                    <c:if test="${ not empty deliveryAddress.phone }">
                        ${fn:escapeXml(deliveryAddress.phone)}
                    </c:if>
                </span>
        </div>
    </c:if>
</div>