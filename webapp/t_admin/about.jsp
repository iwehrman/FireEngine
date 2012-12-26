<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="java.sql.*" %>
About FireEngine<br />
<% 
FireEngine fe = FireEngine.getInstance(); 
FireEngineContext context = Servlets.getFireEngineContext(application);
String vRoot = context.getVirtualRoot();
%>
<table>
	<tr>
		<th colspan = "2" align = "left">FireEngine</th>
	</tr>
	<tr>
		<td align = "right">Version: </td><td align = "left"><%=fe.getVersion()%></td>
	</tr>
	<tr>
		<td align = "right">Uptime: </td><td align = "left"><%=Dates.formatTime(System.currentTimeMillis() - fe.getStartTime())%></td>
	</tr>
	<tr>
		<td align = "right">Admin: </td><td align = "left"><a href = "mailto:<%=context.getAdmin()%>"><%=context.getAdmin()%></a></td>
	</tr>
	<tr>
		<td align = "right">Validated: </td><td align = "left"><%=System.getProperty("com.labfire.fe.validate")%></td>
	</tr>
	<tr>
		<td align = "right">Virtual Root: </td><td align = "left"><%=vRoot%></td>
	</tr>
	<tr>
		<td align = "right">Physical Root: </td><td align = "left"><%=application.getRealPath("/")%></td>
	</tr>
	<tr>
		<td align = "right">Copyright: </td><td align = "left">&copy; 2002 <a href = "http://labfire.com/">Labfire, Inc.</a> All Rights Reserved.</td>
	</tr>
	<tr>
		<td><br /></td><td align = "left">This application is powered by <span style="color:#c00">FireEngine</span>. <br />Contact <a href = "mailto:info@labfire.com">info@labfire.com</a>, or visit <a href = "http://labfire.com/">http://labfire.com/</a> for more information.</td>
	</tr>
</table>
