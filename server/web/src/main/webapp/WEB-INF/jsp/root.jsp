<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="openjudge" tagdir="/WEB-INF/tags/openjudge" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>OpenJudge</title>
    <link href="<c:url value='/resources/css/style.css' />" rel="stylesheet"/>
</head>

<body>

<openjudge:mainMenu/>

<div style="margin-top:60px;">
    <h1>${message}</h1>
</div>

<openjudge:footer/>
</body>
</html>
