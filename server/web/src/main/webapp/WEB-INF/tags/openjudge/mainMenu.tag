<%--suppress ELValidationInJSP --%>
<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<ul>
	<li><a href="<c:url value="/"/>">Home</a></li>
	<li><a href="<c:url value="/contests"/>">Contests</a></li>
	<li style="float:right"><a href="<c:url value="/login"/>">Log in</a></li>
	<li><a href="javascript:logout()">Log out</a></li>
</ul>