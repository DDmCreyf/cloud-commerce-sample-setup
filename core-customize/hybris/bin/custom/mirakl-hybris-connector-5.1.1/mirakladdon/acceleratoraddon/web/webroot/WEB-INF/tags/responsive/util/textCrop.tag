<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="text" required="true" type="java.lang.String" %>
<%@ attribute name="maxLength" required="true" type="java.lang.Integer" %>

<c:choose>
    <c:when test="${fn:length(text) <= maxLength}">
        ${text}
    </c:when>
    <c:otherwise>
        ${fn:substring(text, 0, maxLength)}...
    </c:otherwise>
</c:choose>
