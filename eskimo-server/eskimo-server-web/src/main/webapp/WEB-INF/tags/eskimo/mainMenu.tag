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
<div class="header">
	<div class="menu">
		<a class="menu__link menu__link_navigation ${currentUrl == homeUrl ? "menu__link_active" : ""}"
		   href="${homeUrl}">Home</a>
		<a class="menu__link menu__link_navigation ${currentUrl == contestsUrl ? "menu__link_active" : ""}"
		   href="${contestsUrl}">Contests</a>
		<a class="menu__link menu__link_user-info ${currentUrl == logoutUrl ? "menu__link_active" : ""}"
		   href="javascript:logout()">Log out</a>
		<a class="menu__link menu__link_user-info ${currentUrl == loginUrl ? "menu__link_active" : ""}"
		   href="${loginUrl}">Log in</a>
	</div>

</div>
