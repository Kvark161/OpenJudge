<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="eskimo" tagdir="/WEB-INF/tags/eskimo" %>
<html>
<head>
	<eskimo:globalHead/>
	<title>Summary</title>
</head>

<body>
<eskimo:contestMenu/>
<div class="container">
	<table class="table">
		<tr>
			<td>Name:</td>
			<td>${contest.name}</td>
		</tr>
		<tr>
			<td>Start time:</td>
			<td>${contest.startTime}</td>
		</tr>
		<tr>
			<td>Duration:</td>
			<td>${contest.duration}</td>
		</tr>
	</table>
</div>
<eskimo:footer/>
</body>
</html>