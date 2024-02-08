<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.AbstractOrderData" %>
<%@ attribute name="orderEntry" required="true" type="de.hybris.platform.commercefacades.order.data.OrderEntryData" %>
<%@ attribute name="consignmentEntry" required="false"
              type="de.hybris.platform.commercefacades.order.data.ConsignmentEntryData" %>
<%@ attribute name="itemIndex" required="true" type="java.lang.Integer" %>
<%@ attribute name="targetUrl" required="false" type="java.lang.String" %>
<%@ attribute name="hybrisversion" required="true" type="java.lang.Double" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<c:url value="${orderEntry.product.url}" var="productUrl"/>
<c:url value="/my-account/consignment/${consignmentEntry.consignmentCode}/open-incident/${consignmentEntry.miraklOrderLineId}" var="openIncidentUrl"/>
<c:url value="/my-account/consignment/${consignmentEntry.consignmentCode}/close-incident/${consignmentEntry.miraklOrderLineId}" var="closeIncidentUrl"/>
<c:set var="entryStock" value="${orderEntry.product.stock.stockLevelStatus.code}"/>

<c:if test="${hybrisversion >= 6.1}">
    <c:set var="cssVersionMarker" value="hybris-6-1-plus"/>
</c:if>

<c:choose>
    <c:when test="${consignmentEntry.miraklOrderLineStatus.code eq 'CANCELED'}">
        <c:set var="textColor" value="text-muted"/>
        <c:set var="backgroundColor" value="bg-muted"/>
    </c:when>
    <c:when test="${consignmentEntry.miraklOrderLineStatus.code eq 'INCIDENT_OPEN'}">
        <c:set var="textColor" value="text-warning"/>
        <c:set var="backgroundColor" value="bg-warning"/>
    </c:when>
    <c:when test="${consignmentEntry.miraklOrderLineStatus.code eq 'REFUSED'}">
        <c:set var="textColor" value="text-danger"/>
        <c:set var="backgroundColor" value="bg-danger"/>
    </c:when>
    <c:otherwise>
        <c:set var="textColor" value=""/>
        <c:set var="backgroundColor" value=""/>
    </c:otherwise>
</c:choose>

<li class="${cssVersionMarker} product-item ${backgroundColor}">
    <div class="row">
        <div class="col-md-9">
            <div class="thumb">
                <ycommerce:testId code="orderDetail_productThumbnail_link">
                    <a href="${productUrl}">
                        <product:productPrimaryImage product="${orderEntry.product}" format="thumbnail"/>
                    </a>
                </ycommerce:testId>
            </div>

            <div class="details">
                <div class="name">
                    <ycommerce:testId code="orderDetails_productName_link">
                        <a href="${orderEntry.product.purchasable ? productUrl : ''}" class="${textColor}">
                                ${fn:escapeXml(orderEntry.product.name)}
                        </a>
                    </ycommerce:testId>
                </div>
                <div class="itemId">
                    <ycommerce:testId code="orderDetails_productCode">
                        ${fn:escapeXml(orderEntry.product.code)}
                    </ycommerce:testId>
                </div>

                <%-- Promotions --%>
                <c:if test="${not empty order.appliedProductPromotions}">
                    <div class="promo">
                        <ul>
                            <c:forEach items="${order.appliedProductPromotions}" var="promotion">
                                <c:set var="displayed" value="false"/>
                                <c:forEach items="${promotion.consumedEntries}" var="consumedEntry">
                                    <c:if test="${not displayed and consumedEntry.orderEntryNumber == orderEntry.entryNumber}">
                                        <c:set var="displayed" value="true"/>
                                        <li>
                                            <ycommerce:testId code="orderDetail_productPromotion_label">
                                                ${promotion.description}
                                            </ycommerce:testId>
                                        </li>
                                    </c:if>
                                </c:forEach>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>

                <%-- Price --%>
                <div>
                    <spring:theme code="basket.page.itemPrice"/>:
                    <span class="price">
                            <ycommerce:testId code="orderDetails_productItemPrice_label">
                                <c:choose>
                                    <c:when test="${not orderEntry.product.multidimensional or (orderEntry.product.priceRange.minPrice.value eq orderEntry.product.priceRange.maxPrice.value)}">
                                        <format:price priceData="${orderEntry.basePrice}" displayFreeForZero="true"/>
                                    </c:when>
                                    <c:otherwise>
                                        <format:price priceData="${orderEntry.product.priceRange.minPrice}"
                                                      displayFreeForZero="true"/>
                                        - <format:price priceData="${orderEntry.product.priceRange.maxPrice}"
                                                        displayFreeForZero="true"/>
                                    </c:otherwise>
                                </c:choose>
                            </ycommerce:testId>
                        </span>
                </div>

                <c:if test="${not empty orderEntry.shopId}">
                    <c:url value="/sellers/${orderEntry.shopId}" var="sellerUrl"/>
                    <div>
                        <spring:theme code="order.entry.seller.label"/>&nbsp;<a href="${sellerUrl}">${orderEntry.shopName}</a>
                    </div>
                </c:if>

                <c:forEach items="${orderEntry.product.baseOptions}" var="option">
                    <c:if test="${not empty option.selected and option.selected.url eq orderEntry.product.url}">
                        <c:forEach items="${option.selected.variantOptionQualifiers}" var="selectedOption">
                            <div>
                                <ycommerce:testId code="orderDetail_variantOption_label">
                                    <span>${fn:escapeXml(selectedOption.name)}:</span>
                                    <span>${fn:escapeXml(selectedOption.value)}</span>
                                </ycommerce:testId>
                            </div>
                            <c:set var="entryStock" value="${option.selected.stock.stockLevelStatus.code}"/>
                        </c:forEach>
                    </c:if>
                </c:forEach>
            </div>
        </div>

        <div class="col-md-3 consignment-status text-right">
            <c:if test="${consignmentEntry ne null}">
                <b class="text-uppercase ${textColor}">${consignmentEntry.miraklOrderLineStatusLabel}</b>
                <div class="consignment-entry-actions">
                    <c:choose>
                        <c:when test="${consignmentEntry.canOpenIncident}">
                            <span class="glyphicon glyphicon-exclamation-sign text-primary" aria-hidden="true"></span>
                            <a href="#" data-incidenturl="${openIncidentUrl}" class="text-primary text-muted js-incidentPopup">
                                <spring:theme code="consignmentEntry.incident.open"/>
                            </a>
                        </c:when>
                        <c:when test="${consignmentEntry.miraklOrderLineStatus.code eq 'INCIDENT_OPEN'}">
                            <a href="#" data-incidenturl="${closeIncidentUrl}" class="text-default text-muted js-incidentPopup">
                                <spring:theme code="consignmentEntry.incident.close"/>
                            </a>
                        </c:when>
                    </c:choose>
                </div>
            </c:if>
        </div>

        <c:set var="showEditableGridClass" value=""/>
        <c:if test="${orderEntry.product.multidimensional}">
            <c:set var="showEditableGridClass"
                   value="with-editable-grid showMultiDGridInOrder updateQuantityProduct-toggle"/>
        </c:if>

        <div class="col-md-12">
            <div class="price-line details ${showEditableGridClass}" data-index="${itemIndex}">
                <div class="qty">
                    <ycommerce:testId code="orderDetails_productQuantity_label">
                        <label><spring:theme code="text.account.order.qty"/>:</label>
                        <span class="qtyValue">
                                <c:choose>
                                    <c:when test="${consignmentEntry ne null }">
                                        ${fn:escapeXml(consignmentEntry.quantity)}
                                    </c:when>
                                    <c:otherwise>
                                        ${fn:escapeXml(orderEntry.quantity)}
                                    </c:otherwise>
                                </c:choose>
                            </span>

                        <c:if test="${consignmentEntry ne null and consignmentEntry.shippedQuantity ne null}">
                            <label><spring:theme code="order.consignment.entry.shipped.quantity"/></label>
                            <span class="qtyValue">${consignmentEntry.shippedQuantity}</span>
                        </c:if>
                    </ycommerce:testId>

                    <%-- total --%>
                    <div class="item-price">
                        <ycommerce:testId code="orderDetails_productTotalPrice_label">
                            <format:price priceData="${orderEntry.totalPrice}" displayFreeForZero="true"/>
                        </ycommerce:testId>
                    </div>
                </div>
            </div>
        </div>
    </div>
</li>
<div id="ajaxGrid${itemIndex}" style="display: none" class="order-grid"></div>
<c:if test="${orderEntry.product.multidimensional}">
    <c:forEach items="${orderEntry.entries}" var="currentEntry" varStatus="stat">
        <c:set var="subEntries"
               value="${stat.first ? '' : subEntries}${currentEntry.product.code}:${currentEntry.quantity},"/>
    </c:forEach>

    <div style="display:none" id="grid${itemIndex}" data-sub-entries="${subEntries}"
         data-order-code="${fn:escapeXml(order.code)}" data-target-url="${targetUrl}">
    </div>
</c:if>
