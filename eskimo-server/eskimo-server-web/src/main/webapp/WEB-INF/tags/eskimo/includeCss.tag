<%--suppress HtmlUnknownTarget --%>
<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:url value="/resources/bootstrap/css/bootstrap.min.css" var="boostrapMinCssUrl"/>
<c:url value="/resources/bootstrap/js/bootstrap.min.js" var="boostrapMinJsUrl"/>
<c:url value="/resources/jquery/jquery.min.js" var="jqueryMinJsUrl"/>
<link href="${boostrapMinCssUrl}" rel="stylesheet"/>
<script src=""
${boostrapMinJsUrl}"/>
<script src=""
${jqueryMinJsUrl}"/>
