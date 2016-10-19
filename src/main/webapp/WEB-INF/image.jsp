<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Instagrim</title>
    <link rel="stylesheet" type="text/css" href="../Styles.css" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
    <a href="<%=request.getAttribute("imgURL")%>"><img src="<%=request.getAttribute("imgSrc")%>"/></a><br>
<%
    String prevImg = (String)request.getAttribute("prev");
    if (prevImg != null) {
%>
    <a href="<%=prevImg%>"><-- Previous</a>
<%
    } else {
%>
    <a href="#"><-- Previous</a>
<%
    }
%>

<%
    String nextImg = (String)request.getAttribute("next");
    if (nextImg != null) {
%>
    <a href="<%=nextImg%>">Next --></a>
<%
    } else {
%>
    <a href="#">Next --></a>
<%
    }
%>
</body>
</html>
