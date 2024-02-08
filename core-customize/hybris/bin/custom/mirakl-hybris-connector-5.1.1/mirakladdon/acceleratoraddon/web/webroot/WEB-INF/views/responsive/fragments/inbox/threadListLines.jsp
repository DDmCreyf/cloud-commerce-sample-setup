<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="inbox" tagdir="/WEB-INF/tags/addons/mirakladdon/responsive/inbox" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
{
    "threadPageContent" : "<spring:escapeBody javaScriptEscape="true" htmlEscape="false"><inbox:threadList threads="${threadsResult.threads}" /></spring:escapeBody>",
    "nextPageToken" : "${threadsResult.nextPageToken}"
}
