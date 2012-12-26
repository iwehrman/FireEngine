<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.cache.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.sql.*" %>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
String vRoot = context.getVirtualRoot();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="en">
<head>
	<title>FireEngine | Reminder</title>
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
<form action = "<%=vRoot%>servlet/com.labfire.fe.auth.LoginReminder" method = "post">
<table cellpadding="2">
	<tr>
		<th align="left">Forgotten Password?</th>
	</tr>
	<tr>
		<td>Enter your your email address below,<br />and your login information will be sent promptly.</td>
	</tr>
	<tr>
		<td>Email: <input type = "text" size = "20" name = "email" /></td>
	</tr>
	<tr>
		<td align = "right"><input type = "submit" value = "Send" /></td>
	</tr>
</table>
</form>		  
</center>
</body>
</html>
