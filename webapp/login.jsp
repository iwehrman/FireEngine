<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.cache.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="com.labfire.fe.template.TemplateService" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.sql.*" %>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
String vRoot = context.getVirtualRoot();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="en">
<head>
	<title>User Login</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<style type="text/css">
		a {
			color: #000;
			text-decoration: none;
		}
		a:hover {
			text-decoration: underline;
		}
		table { font-size: 11.5px; font-family: Helvetica,Verdana,Arial,sans-serif; padding: 8px; background: #eee; border: 1px solid black; border-right: 2px solid grey; border-bottom: 2px solid grey; }
		th { color: #c00; }
		td { font-size: 10px; font-family: Helvetica,Verdana,Arial,sans-serif; }
		.hierarchy {font-size: 8px; color:#c00;}
		.head {font-size: 18px;	line-height: 18px; 	font-weight: bold; color:#c00;}
		body { font-size: 10px; font-family: Helvetica,Verdana,Arial,sans-serif; 
			background-color: white;
			background-image: url(http://www.labfire.com/lab6_bg.png); 
			background-position: bottom right;
			background-repeat: no-repeat;
		}
		input {
			border:1px solid #bbb;
			background-color:white;
			vertical-align:middle;
			height: 16px;
		}
		input:focus{
			border:1px solid #999;
		}
	</style>
</head>

<body>
<p /><p />
<center>
<%
String errorMessage = (String)request.getAttribute("errorMessage");
if (errorMessage != null) {
%>
<table cellpadding="2">
	<tr>
		<th>Error: Unable to login user</th>
	</tr>
	<tr>
		<td><%=errorMessage%></td>
	</tr>
</table>
<p /><p />
<%
}
%>
<form action = "<%=vRoot%>servlet/com.labfire.fe.auth.AuthenticationServlet" method = "post">
<table cellpadding="2">
	<tr>
		<th align="right">User Login</th>
		<th align="left"><br /></th>
	</tr>
	<tr>
		<td align = "right" nowrap = "nowrap">User name:</td>
		<td align = "left"><input type = "text" size = "20" name = "FireEngineUser" /></td>
	</tr>
	<tr>
		<td align = "right" nowrap = "nowrap">Password:</td>
		<td align = "left"><input type = "password" size = "20" name = "FireEnginePass" /></td>
	</tr>
	<tr>
		<td align = "right" nowrap = "nowrap">Remember me:</td>
		<td align = "left"><input type = "checkbox" name = "FireEngineCookie" /></td>
	</tr>
	<tr>
		<td align = "right" colspan = "2"><input type = "submit" value = "Login" /></td>
	</tr>
</table>
<a href="<%=vRoot%>reminder.jsp">Forgotten Password?</a>
<%
String requestURI = (String)request.getAttribute("requestURI");
if (requestURI == null || requestURI.length() == 0)
	requestURI = request.getParameter("requestURI");
if (requestURI == null || requestURI.length() == 0) {
	requestURI = vRoot;
} else if (requestURI.lastIndexOf(context.getLoginPage()) != -1) {
	String servletName = TemplateService.getServletName();
	requestURI = vRoot + servletName;
}
%>
<input type = "hidden" name = "requestURI" value = "<%= requestURI %>" />
</form>		  
</center>
</body>
</html>
