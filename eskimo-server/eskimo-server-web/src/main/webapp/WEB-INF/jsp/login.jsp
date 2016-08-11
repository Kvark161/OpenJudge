<%--suppress ELValidationInJSP --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="eskimo" tagdir="/WEB-INF/tags/eskimo" %>
<html>
<head>
	<eskimo:globalHead/>
	<title>Log in</title>
</head>
<body onload='document.loginForm.username.focus();'>

<eskimo:mainMenu/>

<c:url value="/signup" var="signupUrl"/>
<c:url value="/j_spring_security_check" var="springSecurityCheckUrl"/>

<div class="container">

	<div id="loginbox" class="mainbox col-md-4 col-md-offset-4 col-sm-6 col-sm-offset-3">
		<div class="panel panel-default">

			<div class="panel-body">

				<form name="form" id="form" class="form-horizontal" role="form" action="${springSecurityCheckUrl}"
				      method='POST'>

					<c:if test="${not empty error}">
						<div class="alert alert-danger">${error}</div>
					</c:if>
					<c:if test="${not empty msg}">
						<div class="alert alert-info">${msg}</div>
					</c:if>

					<div class="input-group">
						<span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
						<input id="user" type="text" class="form-control" name="username" value="" placeholder="User">
					</div>

					<div class="input-group">
						<span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
						<input id="password" type="password" class="form-control" name="password"
						       placeholder="Password">
					</div>

					<div class="form-group">
						<!-- Button -->
						<div class="col-sm-12 controls">
							<button type="submit" href="#" class="btn btn-primary pull-right"><i
									class="glyphicon glyphicon-log-in"></i> Log in
							</button>
						</div>
					</div>
					<input type="hidden" name="${_csrf.parameterName}"
					       value="${_csrf.token}"/>
				</form>

			</div>
		</div>
	</div>
</div>

<eskimo:footer/>
</body>
</html>

