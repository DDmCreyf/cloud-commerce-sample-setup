<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="inbox" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/inbox"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<template:page pageTitle="${pageTitle}">

    <spring:htmlEscape defaultHtmlEscape="true" />
    <div class="account-section">

        <div class="account-section-content">

            <div class="back-link">
                <c:url value="/my-account/order/${orderCode}" var="orderUrl" />
                <a href="${orderUrl}">
                    <span class="glyphicon glyphicon-chevron-left"></span>
                </a>
                <span class="label">
                    <c:choose>
                        <c:when test="${not empty thread}">
                            <fmt:formatDate value="${thread.dateUpdated}" dateStyle="medium" timeStyle="short" type="both" var="lastUpdateDate"/>
                            ${fn:escapeXml(thread.topic.displayValue)} - ${lastUpdateDate}
                        </c:when>
                        <c:otherwise>
                            <spring:theme code="inbox.thread.header.new"/>
                        </c:otherwise>
                    </c:choose>
                </span>
                
                <div class="title-conversation-participants">
                    <c:if test="${not empty thread.messages}">
                        <spring:theme code="inbox.thread.header.participants"/>&nbsp;
                        <c:forEach var="participant" items="${thread.currentParticipants}" varStatus="counter">
                            <c:out value="${fn:escapeXml(participant.displayName)}${counter.index < fn:length(thread.currentParticipants) - 1 ? ', ' : ''}" />
                        </c:forEach>
                    </c:if>
                </div>
            </div>

            <c:if test="${not empty thread.messages}">
                <inbox:threadMessages consignmentCode="${consignmentCode}" thread="${thread}" />
                <hr />
            </c:if>

            <c:set value="${empty thread}" var="isNewMessage" />
            <c:choose>
                <c:when test="${isNewMessage}">
                    <c:url value="/my-account/inbox/thread" var="submitMessageURL" />
                </c:when>
                <c:otherwise>
                    <c:url value="/my-account/inbox/thread/${thread.id}" var="submitMessageURL" />
                </c:otherwise>
            </c:choose>

            <inbox:composeMessage selectableParticipants="${selectableParticipants}" consignmentCode="${consignmentCode}" thread="${thread}" submitMessageURL="${submitMessageURL}" />

        </div>
    </div>
</template:page>
