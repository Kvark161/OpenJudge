<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>OpenJudge</title>
    <link href="<c:url value='/static/css/style.css' />" rel="stylesheet">/>
    <link href="<c:url value='/static/css/contests.css' />" rel="stylesheet">/>
</head>

<body>

<jsp:include page="fragments/contestHeader.jsp" />

<div style="margin-top:60px;">
    ${message}
</div>
</body>
</html>