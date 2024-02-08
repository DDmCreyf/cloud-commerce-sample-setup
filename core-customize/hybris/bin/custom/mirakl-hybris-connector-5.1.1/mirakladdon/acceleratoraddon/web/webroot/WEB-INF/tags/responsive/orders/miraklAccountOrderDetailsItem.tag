<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="consignment" required="true" type="de.hybris.platform.commercefacades.order.data.ConsignmentData" %>
<%@ attribute name="hybrisversion" required="true" type="java.lang.Double" %>
<%@ attribute name="inProgress" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order" %>
<%@ taglib prefix="miraklOrder" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/orders" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set value="well well-tertiary" var="cssWell"/>
<c:set value="product-list" var="cssProductList"/>
<c:set value="item-list-header" var="cssItemListHeader"/>

<c:if test="${hybrisversion >= 6.1}">
    <c:set value="item-list" var="cssProductList"/>
    <c:set value="well well-quinary well-xs" var="cssWell"/>
</c:if>
<c:if test="${hybrisversion >= 6.2}">
    <c:set value="item__list" var="cssProductList"/>
    <c:set value="item__list--header" var="cssItemListHeader"/>
</c:if>

<c:set var="isMarketplaceConsignment" value="${consignment.marketplaceStatus ne null}"/>
<c:set var="consignmentStatus"
       value="${(consignment.marketplaceStatus ne null)?consignment.marketplaceStatus:consignment.statusDisplay}"/>

<c:if test="${not inProgress and consignment.status.code eq 'READY_FOR_PICKUP'}" >
    <h2><spring:theme code="text.account.order.warning.storePickUpItems" text="Reminder - Please pick up your items(s) soon."/></h2>
</c:if>

<div class="${cssWell}">
    <ycommerce:testId code="orderDetail_itemHeader_section">
        <div class="well-headline">
            <ycommerce:testId code="orderDetail_consignmentStatus_label">
                <c:choose>
                    <c:when test="${isMarketplaceConsignment}">
                        ${consignment.marketplaceStatusLabel}
                    </c:when>
                    <c:otherwise>
                        <spring:theme code="text.account.order.consignment.status.${consignment.statusDisplay}"/>
                    </c:otherwise>
                </c:choose>
            </ycommerce:testId>

            <ycommerce:testId code="orderDetail_consignmentStatusDate_label">
				<span class="well-headline-sub">
                    <fmt:formatDate value="${consignment.statusDate}" dateStyle="medium" timeStyle="short" type="both"/>
                </span>
            </ycommerce:testId>
        </div>

        <div class="well-content col-sm-12 col-md-9">
            <c:choose>
                <c:when test="${consignment.deliveryPointOfService ne null}">
                    <ycommerce:testId code="orderDetail_storeDetails_section">
                        <order:storeAddressItem deliveryPointOfService="${consignment.deliveryPointOfService}"
                                                inProgress="${inProgress}" statusDate="${consignment.statusDate}"/>
                    </ycommerce:testId>
                </c:when>
                <c:otherwise>
                    <div class="row">
                        <div class="col-sm-6 col-md-4 order-ship-to">
                            <ycommerce:testId code="orderDetail_deliveryAddress_section">
                                <div class="label-order"><spring:theme code="text.account.order.shipto"/></div>
                                <div class="value-order"><order:addressItem address="${orderData.deliveryAddress}"/></div>
                            </ycommerce:testId>
                        </div>

                        <div class="col-sm-6 col-md-4 order-shipping-method">
                            <ycommerce:testId code="orderDetail_deliveryMethod_section">
                                <c:choose>
                                    <c:when test="${consignment.shippingModeLabel ne null}">
                                        <div>
                                            <div class="label-order"><spring:theme code="text.shippingMethod"/></div>
                                                ${consignment.shippingModeLabel}
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <order:deliveryMethodItem order="${orderData}"/>
                                    </c:otherwise>
                                </c:choose>
                            </ycommerce:testId>
                        </div>

                        <div class="hidden-sm hidden-xs col-md-4 account-consignment-id">
                            <div class="label-order"><spring:theme code="order.consignment.id"/></div>
                            <div class="value-order">${consignment.code}</div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="well-content col-sm-12 col-md-3 consignment-actions">
            <c:if test="${not inProgress and consignment.customerDebited}">
                <c:if test="${consignment.marketplaceStatus eq 'SHIPPING' or consignment.marketplaceStatus eq 'SHIPPED'
                or consignment.marketplaceStatus eq 'TO_COLLECT'}">
                    <c:url value="/my-account/consignment/${consignment.code}/receive" var="confirmReceptionURL"/>
                    <form:form method="post" action="${confirmReceptionURL}">
                        <button type="submit" class="btn btn-success btn-block">
                            <span class="glyphicon glyphicon-ok"></span> <spring:theme
                                code="order.consignment.confirm.reception"/>
                        </button>
                    </form:form>
                </c:if>
                <c:if test="${consignment.marketplaceStatus eq 'RECEIVED' and consignment.canEvaluate}">
                    <c:url value="/my-account/consignment/${consignment.code}/evaluate" var="evaluateReceptionURL"/>
                    <a role="button" href="${evaluateReceptionURL}" class="btn btn-success btn-block">
                        <span class="glyphicon glyphicon-star"></span> <spring:theme code="order.consignment.evaluate"/>
                    </a>
                </c:if>
             </c:if>
             <div class="row">
                 <c:if test="${consignment.canWriteMessage}">
                     <div class="col-md-6 col-sm-6 col-xs-6 text-center">
                         <c:url value="/my-account/inbox?consignmentCode=${consignment.code}" var="consignmentMessagesURL"/>
                         <a href="${consignmentMessagesURL}" class="text-primary">
                             <span class="glyphicon glyphicon-pencil"></span> <spring:theme code="order.consignment.messages"/>
                         </a>
                     </div>
                 </c:if>
                 <c:if test="${not empty consignment.trackingID}">
                     <div class="col-md-6 col-sm-6 col-xs-6 text-center">
                         <a href="${consignment.trackingID}" target="_blank" class="text-primary">
                             <span class="glyphicon glyphicon-globe"></span> <spring:theme code="order.consignment.track.parcel"/>
                         </a>
                     </div>
                 </c:if>
                 <c:if test="${not empty consignment.documents}">
                     <div class="col-md-6 col-sm-6 col-xs-6 text-center">
                         <a href="#" class="text-primary js-documentsPopup text-muted" data-documentslist="${consignment.code}-documents" data-popuptitle="<spring:theme code="consignment.documents.popup.title"/>">
                             <span class="glyphicon glyphicon-file"></span> <spring:theme code="consignment.documents.link" arguments="${fn:length(consignment.documents)}"/>
                         </a>
                     </div>
                     <div class="documents-popup-holder" id="${consignment.code}-documents">
                         <div class="documents-popup-content">
                             <table class="table table-hover">
                                 <thead>
                                     <tr>
                                     <td></td>
                                     <td><spring:theme code="consignment.documents.file.name"/></td>
                                     <td class="hidden-xs"><spring:theme code="consignment.documents.file.date"/></td>
                                 </tr>
                                 </thead>
                                 <tbody>
                                 <c:forEach var="document" items="${consignment.documents}">
                                     <c:url var="downloadDocumentUrl" value="/my-account/consignment/${consignment.code}/document/${document.code}"/>
                                     <tr class="document-line" data-href="${downloadDocumentUrl}">
                                         <td><span class="glyphicon glyphicon-file" aria-hidden="true"></span></td>
                                         <td>${document.fileName}</td>
                                         <td class="hidden-xs"><fmt:formatDate type="date" value="${document.dateUploaded}"/></td>
                                     </tr>
                                 </c:forEach>
                                 </tbody>
                             </table>
                             <c:url var="downloadAllDocumentsUrl" value="/my-account/consignment/${consignment.code}/documents"/>
                             <a href="${downloadAllDocumentsUrl}" role="button" class="btn btn-primary btn-block download-all-documents">
                                 <span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span> <spring:theme code="consignment.documents.download.all"/>
                             </a>
                         </div>
                     </div>
                 </c:if>
             </div>
            
        </div>

    </ycommerce:testId>
</div>

<ul class="${cssProductList}">

    <c:if test="${hybrisversion >= 6.1}">
        <li class="hidden-xs hidden-sm">
            <ul class="${cssItemListHeader}">
                <li class="product-list-header"><spring:theme code="order.consignment.products" /></li>
            </ul>
        </li>
    </c:if>

    <ycommerce:testId code="orderDetail_itemBody_section">
        <c:forEach items="${consignment.entries}" var="entry" varStatus="loop">
            <miraklOrder:miraklOrderEntryDetails orderEntry="${entry.orderEntry}" consignmentEntry="${entry}" order="${order}" itemIndex="${loop.index}" hybrisversion="${hybrisversion}"/>
        </c:forEach>
    </ycommerce:testId>
</ul>
