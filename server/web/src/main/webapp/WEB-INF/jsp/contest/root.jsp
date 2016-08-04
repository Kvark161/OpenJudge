<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="openjudge" tagdir="/WEB-INF/tags/openjudge" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>OpenJudge</title>
	<openjudge:includeCss/>
</head>

<body>

<openjudge:contestMenu/>

<div style="margin-top:60px;">
	${contest.name}
</div>
<openjudge:footer/>
</body>
</html>