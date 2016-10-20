<%@ page import="io.drakon.uni.ac32007.instagrim.lib.ServletUtils" %>
<%@ page import="io.drakon.uni.ac32007.instagrim.stores.Comment" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Instagrim</title>
    <link rel="stylesheet" type="text/css" href="../Styles.css"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<p><a href="<%=request.getAttribute("imgURL")%>"><img src="<%=request.getAttribute("imgSrc")%>"/></a></p>

<p>
<%
    String prevImg = (String) request.getAttribute("prev");
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
    String nextImg = (String) request.getAttribute("next");
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
</p>

<p>
    <h2>Comments</h2>
<%
    List<Comment> comments = (List<Comment>)request.getAttribute("comments");
    if (comments.isEmpty()) {
%>
    There are no comments for this image.
<%
    } else {
        for (Comment c : comments) {
%>
    <p>
        <h4><%=c.getUser()%> (at <%=c.getTime()%>)</h4>
        <p><%=c.getComment()%></p>
    </p>
<%
        }
    }
%>
</p>

<%
    if ((boolean)request.getAttribute("loggedIn")) {
%>
<p>
    <h2>Add your own comment</h2>
    <form method="POST" action="<%=ServletUtils.INSTANCE.getPathForHTML(request, "/Comment")%>">
        <input name="pic" readonly value="<%=request.getAttribute("uuid")%>" type="hidden">
        <ul>
            <li><textarea title="Comment" name="comment" cols="64" rows="8"></textarea></li>
        </ul><br/>
    <input type="submit" value="Submit">
</form>
</p>
<%
    }
%>

<footer>
    <ul>
        <li class="footer"><a href="<%=ServletUtils.INSTANCE.getPathForHTML(request, "/")%>">Home</a></li>
    </ul>
</footer>
</body>
</html>
