<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="consignmentCode" required="false"%>
<%@ attribute name="thread" required="true" type="com.mirakl.hybris.beans.ThreadData"%>

<%@ taglib prefix="inbox" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/inbox"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="miraklutil" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/util"%>

<div class="container-fluid messaging-pane">
    <c:forEach items="${thread.messages}" var="message">
        <div class="row ${message.isFromCustomer ? 'client' : 'seller'}">
            <c:if test="${message.isFromCustomer}">
                <div class="col-md-4 hidden-sm hidden-xs author">
                    <spring:theme code="consignment.messages.myself" />
                </div>
            </c:if>
            <div class="col-md-8 bubble">
                <div class="body">${ycommerce:sanitizeHTML(message.body)}</div>
                <div class="date">
                    <fmt:formatDate value="${message.dateCreated}" dateStyle="medium" timeStyle="short" type="both" />
                </div>
                <div class="recipient">
                    <spring:theme code="inbox.thread.message.sentto" />
                    <c:forEach var="recipient" items="${message.to}" varStatus="counter">
                        <c:out value=" ${fn:escapeXml(recipient.displayName)}${counter.index < fn:length(message.to) - 1 ? ' & ' : ''}" />
                    </c:forEach>
                </div>
                <c:if test="${not empty message.attachments}">
                    <c:forEach var="attachment" items="${message.attachments}">
                        <c:url var="fileUrl" value="/my-account/inbox/attachment/${attachment.id}" />
                        <a href="${fileUrl}" role="button" class="attachment">
                            <span class="glyphicon glyphicon-file" aria-hidden="true"></span>
                            <miraklutil:textCrop text="${fn:escapeXml(attachment.name)}" maxLength="14" />
                            <span class="file-size">
                                (<fmt:formatNumber type="number" value="${attachment.size/1000}" maxFractionDigits="0" groupingUsed="true" />kb)
                            </span>
                        </a>
                    </c:forEach>
                </c:if>
            </div>
            <c:if test="${not message.isFromCustomer}">
                <div class="col-md-4 hidden-sm hidden-xs author">${fn:escapeXml(message.senderDisplayName)}</div>
            </c:if>
        </div>
    </c:forEach>
</div>

