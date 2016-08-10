<%--suppress ELValidationInJSP --%>
<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<form action="<c:url value="/logout"/>" method="post" id="logoutForm">
	<input type="hidden"
	       name="${_csrf.parameterName}"
	       value="${_csrf.token}"/>
</form>

<%--suppress JSUnusedLocalSymbols --%>
<script>
	function logout() {
		document.getElementById("logoutForm").submit();
	}
</script>

<c:url value="/" var="homeUrl"/>
<c:url value="/contests" var="contestsUrl"/>
<c:url value="/login" var="loginUrl"/>
<c:url value="/logout" var="logoutUrl"/>

<c:set var="currentUrl" value="${requestScope['javax.servlet.forward.request_uri']}"/>
<div class="navbar navbar-default">
	<div class="container-fluid">
		<div class="navbar-header">
			<a class="navbar-brand" href="#">WebSiteName</a>
		</div>
		<ul class="nav navbar-nav">
			<li class="active"><a href="${homeUrl}">Home</a></li>
			<li><a href="${contestsUrl}">Contests</a></li>
		</ul>
		<ul class="nav navbar-nav navbar-right">
			<li><a href="#"><span class="glyphicon glyphicon-user"></span> Sign Up</a></li>
			<li><a href="${loginUrl}"><span class="glyphicon glyphicon-log-in"></span> Login</a></li>
		</ul>
	</div>

</div>
