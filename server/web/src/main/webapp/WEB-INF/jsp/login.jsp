<%--suppress ALL --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="openjudge" tagdir="/WEB-INF/tags/openjudge" %>
<html>
<head>
	<title>Log in</title>
	<openjudge:includeCss/>
	<c:url value="/j_spring_security_check" var="springSecurityCheck"/>
</head>
<body onload='document.loginForm.username.focus();'>

<openjudge:mainMenu/>

<div class="login">

	<c:if test="${not empty error}">
		<div class="login__message login__message_error">${error}</div>
	</c:if>
	<c:if test="${not empty msg}">
		<div class="login__message">${msg}</div>
	</c:if>

	<form name='loginForm' action="${springSecurityCheck}" method='POST'>

		<table class="login-table">
			<tr class="login-table__item">
				<td><input class="login__input" placeholder="Username" type='text' name='username' value=''></td>
			</tr>
			<tr class="login-table__item">
				<td><input class="login__input" placeholder="Password" type='password' name='password'/></td>
			</tr>
			<tr class="login-table__item">
				<td colspan='2'><input class="login__button" name="login" type="submit" value="Log in"/></td>
			</tr>
		</table>

		<input type="hidden" name="${_csrf.parameterName}"
		       value="${_csrf.token}"/>

	</form>
</div>

</body>
</html>
