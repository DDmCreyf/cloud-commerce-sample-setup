<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="miraklutil" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/util" %>

<template:page pageTitle="${pageTitle}">

    <div class="account-section">

        <div class="account-section-content">

            <div class="back-link">
                <c:url value="/my-account/order/${orderCode}" var="orderUrl"/>
                <a href="${orderUrl}">
                    <span class="glyphicon glyphicon-chevron-left"></span>
                </a>
                <span class="label"><spring:theme code="consignment.messages.header"/></span>
            </div>

            <div class="container-fluid messaging-pane">
                <c:forEach items="${messages}" var="message">
                    <div class="row ${message.isFromCustomer ? "client" : "seller"}">
                        <c:if test="${message.isFromCustomer}">
                            <div class="col-md-4 hidden-sm hidden-xs author"><spring:theme code="consignment.messages.myself"/></div>
                        </c:if>
                        <div class="col-md-8 bubble">
                            <span class="subject">${message.subject}</span>
                            <div class="body">${message.body}</div>
                            <c:if test="${not empty message.documents}">
                                <div class="documents">
                                    <span class="subject"><spring:theme code="consignment.messages.attachments"/></span>
                                    <c:forEach var="document" items="${message.documents}">
                                        <c:url var="fileUrl" value="/my-account/consignment/${consignmentCode}/document/${document.code}"/>
                                        <a href="${fileUrl}" role="button" class="attachment">
                                            <span class="glyphicon glyphicon-file" aria-hidden="true"></span>
                                            <miraklutil:textCrop text="${document.fileName}" maxLength="14"/>
                                            <span class="file-size">
                                                (<fmt:formatNumber type="number" value="${document.fileSize/1000}" maxFractionDigits="0" groupingUsed="true"/>kb)
                                            </span>
                                        </a>
                                    </c:forEach>
                                </div>
                            </c:if>
                            <div class="date"><fmt:formatDate value="${message.dateCreated}" dateStyle="medium" timeStyle="short" type="both"/></div>
                        </div>
                        <c:if test="${not message.isFromCustomer}">
                            <div class="col-md-4 hidden-sm hidden-xs author">${message.author}</div>
                        </c:if>
                    </div>
                </c:forEach>
            </div>

            <c:if test="${not empty messages}"><hr/></c:if>

            <form:form method="post" class="messaging-form">

                <div class="form-description">
                    <spring:theme code="consignment.messages.form.description" arguments="${consignmentCode}"/>
                </div>

                <div class="form-group">
                    <label><spring:theme code="consignment.messages.form.subject"/></label>
                    <input name="subject" type="text" class="form-control" value="${lastSubject}">
                </div>

                <div class="form-group">
                    <label><spring:theme code="consignment.messages.form.message"/></label>
                    <textarea name="body" class="form-control" rows="2"></textarea>
                </div>

                <button class="btn btn-primary" type="submit" value="Send message">
                    <spring:theme code="consignment.messages.form.submit"/>
                </button>
            </form:form>

        </div>
    </div>
</template:page>
