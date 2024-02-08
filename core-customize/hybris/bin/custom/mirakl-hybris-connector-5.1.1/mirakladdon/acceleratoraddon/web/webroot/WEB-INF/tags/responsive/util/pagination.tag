<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="currentPage" required="true" type="java.lang.Long" %>
<%@ attribute name="totalPages" required="true" type="java.lang.Long" %>
<%@ attribute name="name" required="false" type="java.lang.String" %>

<c:choose>
    <c:when test="${totalPages <= 5}">
        <c:set var="firstPage" value="1"/>
    </c:when>

    <c:when test="${currentPage <= 3}">
        <c:set var="firstPage" value="1"/>
    </c:when>

    <c:when test="${currentPage >= totalPages - 2}">
        <c:set var="firstPage" value="${totalPages - 4}"/>
    </c:when>

    <c:otherwise>
        <c:set var="firstPage" value="${currentPage - 2}"/>
    </c:otherwise>
</c:choose>

<div class="${name}">
    <ul class="pagination">
        <li class="pagination-prev ${(currentPage <= 1) ? "disabled" : ""}">
            <a href="#" class="glyphicon glyphicon-chevron-left mirakl-pagination-previous"></a>
        </li>
        <c:forEach var="i" begin="${firstPage}" end="${firstPage + 4}">
            <c:if test="${i <= totalPages}">
                <li class="page-list ${(i == currentPage) ? "active" : ""}">
                    <a href="#"><span class="page-link"><c:out value="${i}"/></span></a>
                </li>
            </c:if>
        </c:forEach>
        <li class="pagination-next ${(currentPage >= totalPages) ? "disabled" : ""}">
            <a href="#" rel="next" class="glyphicon glyphicon-chevron-right mirakl-pagination-next"></a>
        </li>
    </ul>
</div>
