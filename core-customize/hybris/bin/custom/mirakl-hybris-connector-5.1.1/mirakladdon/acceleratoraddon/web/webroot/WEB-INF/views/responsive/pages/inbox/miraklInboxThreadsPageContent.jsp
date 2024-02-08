<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="inbox" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/inbox"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:set var="threads" value="${threadsResult.threads}" />
<c:set var="nextPageToken" value="${threadsResult.nextPageToken}" />

<template:page pageTitle="${pageTitle}">
    <spring:htmlEscape defaultHtmlEscape="true" />

    <div class="account-section">

        <div class="account-section-content">

            <div class="account-section-header">
                <c:choose>
                    <c:when test="${not empty consignmentCode}">
                        <spring:theme code="inbox.threads.page.consignement.title" arguments="${consignmentCode}" />
                    </c:when>
                    <c:otherwise>
                        <spring:theme code="inbox.threads.page.title" />
                    </c:otherwise>
                </c:choose>
            </div>

            <c:if test="${empty threads}">
                <div class="account-section-content content-empty">
                    <spring:theme code="inbox.threads.empty" />
                </div>
            </c:if>

            <div id="threadlist-content">
                <c:if test="${not empty threads}">
                    <div class="account-section-content">
                        <div class="account-threadlist">
                            <div class="account-threadlist-overview-table">
                                <table id="thread-list" class="threadlist-list-table responsive-table">
                                    <tr class="account-threadlist-table-head responsive-table-head hidden-xs">
                                        <th>
                                            <spring:theme code="inbox.threads.list.participants" />
                                        </th>
                                        <th>
                                            <spring:theme code="inbox.threads.list.entity" />
                                        </th>
                                        <th>
                                            <spring:theme code="inbox.threads.list.topic" />
                                        </th>
                                        <th>
                                            <spring:theme code="inbox.threads.list.lastmessagedate" />
                                        </th>
                                    </tr>
                                    <inbox:threadList threads="${threads}" />
                                </table>
                            </div>
                        </div>
                    </div>
                </c:if>
                <c:if test="${not empty nextPageToken}">
                    <div class="row text-center">
                        <input type="hidden" id="nextPageToken" value="${nextPageToken}">
                        <a role="button" id="more-button" href="#" class="btn btn-default">
                            <spring:theme code="inbox.threads.button.morethreads" />
                        </a>
                    </div>
                </c:if>

                <c:if test="${not empty consignmentCode}">
                    <div class="row text-center">
                        <input type="hidden" id="consignmentCode" value="${consignmentCode}">
                        <c:url value="/my-account/inbox/thread?consignmentCode=${consignmentCode}" var="newThreadURL" />
                        <a role="button" href="${newThreadURL}" class="btn btn-default">
                            <spring:theme code="inbox.threads.button.newthread" />
                        </a>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</template:page>