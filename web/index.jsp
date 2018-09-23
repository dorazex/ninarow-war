<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<%@page import="servlets.*" %>

<head>
    <title>Nonogram login</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- All the files that are required -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.6.3/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/mdb.min.css">
    <link rel="stylesheet" href="css/login-style.css">
    <link rel="stylesheet" href="css/common-style.css">
    <link href='http://fonts.googleapis.com/css?family=Varela+Round' rel='stylesheet' type='text/css'>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"/>
</head>
<body>
<!-- Where all the magic happens -->
<!-- LOGIN FORM -->
<div id="wrap">
    <div align="center" class="text-center container" id="main" style="padding:50px 0;">
        <div class="logo">Nonogram Login</div>
        <!-- Main Form -->
        <div class="login-form-1">
            <form method="GET" action="login">
                <div class="main-login-form">
                    <div class="login-group">
                        <div class="form-group">
                            <input type="text" class="form-control" name="username" placeholder="username">
                        </div>
                        <div class="form-group">
                            <select name="playertype" class="form-control" style="width:100%; max-width:100%;">
                                <option value="Human">Human</option>
                                <option value="Computer">Computer</option>
                            </select>
                        </div>
                        <div class="form-group login-group-checkbox">
                            <input type="checkbox" id="lg_remember" name="loggedin">
                            <label for="lg_remember">Already logged in?</label>
                        </div>
                    </div>
                    <button type="submit" value="Login" class="login-button"><i class="fa fa-chevron-right"
                                                                                aria-hidden="true"></i></button>
                </div>
            </form>
        </div>
        <!-- end:Main Form -->
    </div>
    <div class="row">
        <div class="alert alert-danger col-md-4 col-md-offset-4" align="center">
            <% Object errorMessage = request.getAttribute(Constants.USER_NAME_ERROR);%>
            <% if (errorMessage != null) {%>
            <%--<span class="label important"></span>--%>
            <strong>Username error: </strong><%=errorMessage%>
            <% } %>
        </div>
    </div>
</div>
<!--footer-->
<footer class="footer elegant-color-dark" id="footer">
    <div class="container">
        <div class="row">
            <div class="span12">
                <div class="col-xs-5">
                    <p class="text-muted">By Sela Oren and Moran Mahabi 2016</p>
                </div>
            </div>
        </div>
    </div>
</footer>
<!--footer end-->
</body>
</html>

