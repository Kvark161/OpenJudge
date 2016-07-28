<%--suppress HtmlUnknownTarget --%>
<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<ul>
	<li><a class="active" href="<c:url value="/"/>">Home</a></li>
	<li><a href="summary">Summary</a></li>
	<li><a href="problems">Problems</a></li>
	<li><a href="submit">Submit</a></li>
	<li><a href="standings">Standings</a></li>
	<li style="float:right"><a href="<c:url value="/login"/>">Login</a></li>
</ul>