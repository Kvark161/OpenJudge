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

<form method="POST" action="${postUrl}?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data">
	File to upload: <input type="file" name="file"/>
	Name: <input type="text" name="name"/>
	<input type="submit" value="Upload"/> Press here to upload the file!
</form>

<eskimo:footer/>
</body>
</html>
