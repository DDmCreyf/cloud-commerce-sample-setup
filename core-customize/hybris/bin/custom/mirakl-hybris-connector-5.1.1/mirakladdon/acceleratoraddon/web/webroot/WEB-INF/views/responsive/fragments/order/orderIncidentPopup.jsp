<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>

<div class="incident-item">
    <div class="thumb"><product:productPrimaryImage product="${product}" format="cartIcon"/></div>
    <div class="details"><c:out value="${product.name}"/></div>
</div>

<form:form method="post">
    <div class="form-group">
        <label for="reasonSelector"><spring:theme code="consignmentEntry.incident.reason.select"/></label>
        <select name="reasonCode" class="form-control" id="reasonSelector">
            <c:forEach items="${reasons}" var="reason">
                <option value="${reason.code}">${reason.label}</option>
            </c:forEach>
        </select>
    </div>
    <div class="form-group">
        <label><spring:theme code="consignmentEntry.incident.reason.message"/></label>
        <textarea name="message" class="form-control" rows="2"></textarea>
    </div>
    <button type="submit" class="btn-warning btn btn-block">
        <spring:theme code="consignmentEntry.incident.submit"/>
    </button>
</form:form>
