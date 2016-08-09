<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="eskimo" tagdir="/WEB-INF/tags/eskimo" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Eskimo</title>
	<eskimo:includeCss/>
</head>

<body>

<eskimo:mainMenu/>

<div style="margin-top:60px;">
	<h2>Contests</h2>

	<table>
		<tr>
			<th>Id</th>
			<th>Name</th>
			<th>Duration</th>
		</tr>
		<c:forEach var="contest" items="${contests}">
			<tr>
				<td>${contest.id}</td>
				<td><a href="<c:url value="/contest/${contest.id}/summary"/>">${contest.name}</a></td>
				<td>Duration</td>
			</tr>
		</c:forEach>
	</table>
</div>
<eskimo:footer/>
</body>
</html>