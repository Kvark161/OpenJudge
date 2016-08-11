<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="eskimo" tagdir="/WEB-INF/tags/eskimo" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Eskimo</title>
	<eskimo:globalHead/>
</head>

<body>

<eskimo:mainMenu/>

<div>
	<div class="container">
		<table class="table table-striped table-bordered">
			<thead>
			<tr>
				<th>ID</th>
				<th>Name</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="contest" items="${contests}">
				<tr>
					<td>${contest.id}</td>
					<td><a href="<c:url value="/contest/${contest.id}/summary"/>">${contest.name}</a></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>

	<eskimo:footer/>
</body>
</html>