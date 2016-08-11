<%--suppress ELValidationInJSP --%>
<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
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
<c:url value="/signup" var="signupUrl"/>
<c:set var="currentUrl" value="${requestScope['javax.servlet.forward.request_uri']}"/>

<div class="navbar navbar-default navnar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="${homeUrl}">Eskimo</a>
		</div>
		<ul class="nav navbar-nav">
			<li class="${currentUrl == homeUrl ? "active" : ""}"><a href="${homeUrl}">Home</a></li>
			<li class="${currentUrl == contestsUrl ? "active" : ""}"><a href="${contestsUrl}">Contests</a></li>
		</ul>
		<ul class="nav navbar-nav navbar-right">
			<sec:authorize access="!isAuthenticated()">
				<li class="${currentUrl == signupUrl ? "active" : ""}"><a href="${signupUrl}"><span
						class="glyphicon glyphicon-user"></span> Sign up</a></li>
				<li class="${currentUrl == loginUrl ? "active" : ""}"><a href="${loginUrl}"><span
						class="glyphicon glyphicon-log-in"></span> Log in</a></li>
			</sec:authorize>
			<sec:authorize access="isAuthenticated()">
				<sec:authentication var="principal" property="principal" />
				<li class="navbar-text">Signed in as ${principal.username}</li>
				<li><a href="javascript:logout()"><span class="glyphicon glyphicon-log-out"></span> Log out</a></li>
			</sec:authorize>
		</ul>
	</div>

</div>
