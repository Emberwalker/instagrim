<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="io.drakon.uni.ac32007.instagrim.stores.*" %>
<%@ page import="io.drakon.uni.ac32007.instagrim.lib.ServletUtils" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="<%=ServletUtils.INSTANCE.getPathForHTML(request, "/Styles.css")%>" />
    </head>
    <body>
        <header>
        
        <h1>InstaGrim ! </h1>
        <h2>Your world in Black and White</h2>
        </header>
        
        <nav>
            <ul>
                <li class="nav"><a href="<%=ServletUtils.INSTANCE.getPathForHTML(request, "/Upload")%>">Upload</a></li>
                <li class="nav"><a href="<%=ServletUtils.INSTANCE.getPathForHTML(request, "/Images/majed")%>">Sample Images</a></li>
            </ul>
        </nav>
 
        <article>
            <h1>Your Pics</h1>
        <%
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
            if (lsPics == null) {
        %>
        <p>No Pictures found</p>
        <%
            } else {
                Iterator<Pic> iterator;
                iterator = lsPics.iterator();
                while (iterator.hasNext()) {
                    Pic p = (Pic) iterator.next();
        %>
        <a href="<%=ServletUtils.INSTANCE.getPathForHTML(request, "/Image/" + p.getSUUID())%>" >
            <img src="<%=ServletUtils.INSTANCE.getPathForHTML(request, "/Thumb/" + p.getSUUID())%>">
        </a><br/>
        <%
                }
            }
        %>
        </article>
        <footer>
            <ul>
                <li class="footer"><a href="<%=ServletUtils.INSTANCE.getPathForHTML(request, "/")%>">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
