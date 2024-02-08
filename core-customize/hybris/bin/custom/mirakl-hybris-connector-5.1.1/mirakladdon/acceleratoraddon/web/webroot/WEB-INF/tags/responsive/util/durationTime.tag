<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="time" required="true" type="java.lang.Long" %>

<c:choose>
    <c:when test="${time/3600>1}">
        <fmt:formatNumber value="${time/3600}" maxFractionDigits="0" />
        <spring:theme code="util.duration.hour"/>
    </c:when>
    <c:otherwise>
        <fmt:formatNumber value="${time/60}" maxFractionDigits="0" />
        <spring:theme code="util.duration.minute"/>
    </c:otherwise>
</c:choose>
