<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="openjudge" tagdir="/WEB-INF/tags/openjudge" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>OpenJudge</title>
	<openjudge:includeCss/>
</head>

<body>

<openjudge:mainMenu/>

<c:url value="/" var="alalala"/>
<c:url value="/" var="homeUrl"/>

<div style="margin-top:60px;">
	<h1>${message}</h1>
	Relative Path: ${requestScope['javax.servlet.forward.request_uri']} AAAAAAAAAAA
	${homeUrl}
</div>
<openjudge:footer/>
</body>
</html>
