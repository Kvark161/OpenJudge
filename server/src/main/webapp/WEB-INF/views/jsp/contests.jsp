<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>OpenJudge</title>
    <link href="<c:url value='/static/css/style.css' />" rel="stylesheet"></link>
    <link href="<c:url value='/static/css/contests.css' />" rel="stylesheet"></link>
</head>

<body>

<jsp:include page="../fragments/mainHeader.jsp" />

<div style="margin-top:60px;">
    <h2>Contests</h2>

    <table>
        <tr>
            <th>Contest</th>
            <th>Start Time</th>
            <th>Duration</th>
        </tr>
        <tr>
            <td>Contest 1</td>
            <td>123123</td>
            <td>300</td>
        </tr>
        <tr>
            <td>Contest 2</td>
            <td>456456</td>
            <td>120</td>
        </tr>
        <tr>
            <td>Contest 3</td>
            <td>789789</td>
            <td>300</td>
        </tr>
    </table>
</div>
</body>
</html>