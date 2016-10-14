<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="../Styles.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <header>
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
        </header>
        <nav>
            <ul>
                <li><a href="./upload.jsp">Upload</a></li>
                    <%  if ((boolean)request.getAttribute("loggedIn")) {  %>
                <li><a href="./Images/<%=(String)request.getAttribute("username")%>">Your Images</a></li>
                    <%  } else {  %>
                <li><a href="./register.jsp">Register</a></li>
                <li><a href="./Login">Login</a></li>
                    <%  }  %>
            </ul>
        </nav>
        <footer>
            <ul>
                <li class="footer"><a href="./">Home</a></li>
                <li>&COPY; Andy C</li>
            </ul>
        </footer>
    </body>
</html>
