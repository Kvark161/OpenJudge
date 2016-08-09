<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="eskimo" tagdir="/WEB-INF/tags/eskimo" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>eskimo</title>
	<eskimo:includeCss/>
</head>

<body>

<eskimo:contestMenu/>

<div style="margin-top:60px;">
	${contest.name}
</div>
<eskimo:footer/>
</body>
</html>