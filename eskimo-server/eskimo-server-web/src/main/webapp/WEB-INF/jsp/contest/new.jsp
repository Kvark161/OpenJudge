<%--suppress ELValidationInJSP --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="eskimo" tagdir="/WEB-INF/tags/eskimo" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<eskimo:globalHead/>
	<title>New contest</title>
</head>
<body>
<eskimo:mainMenu/>

<c:url value="/contests/new/zip" var="postUrl"/>

<div class="container">

	<form method="POST" action="${postUrl}?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data">
		<div class="form-group">
			File to upload: <input type="file" name="file"/>
		</div>
		<input type="submit" value="Create"/>
	</form>

	<c:if test="${!empty error}">
		<div class="alert alert-danger">
			<strong>Fail! </strong>${error}
		</div>
	</c:if>

</div>

<eskimo:footer/>
</body>
</html>
