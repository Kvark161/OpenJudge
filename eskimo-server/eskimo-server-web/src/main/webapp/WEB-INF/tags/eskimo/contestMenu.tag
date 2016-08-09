<%--suppress HtmlUnknownTarget --%>
<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="contest" scope="request" type="com.klevleev.eskimo.server.core.domain.Contest"/>

<c:url value="/" var="homeUrl"/>
<c:url value="/contest/${contest.id}/summary" var="summaryUrl"/>
<c:url value="/contest/${contest.id}/problems" var="problemsUrl"/>
<c:url value="/contest/${contest.id}/submit" var="submitUrl"/>
<c:url value="/login" var="loginUrl"/>

<ul>
	<li><a href="${homeUrl}">Home</a></li>
	<li><a href="${summaryUrl}">Summary</a></li>
	<li>Problems</li>
	<li>Submit</li>
	<li>Standings</li>
	<li style="float:right"><a href="${loginUrl}">Login</a></li>
</ul>