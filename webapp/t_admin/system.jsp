<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="java.sql.*" %>
<% 
FireEngine fe = FireEngine.getInstance();
%>
<table>
	<tr>
		<th colspan = "2" align = "left">System</th>
	</tr>
	<tr>
		<td align = "right">Operating System: </td><td align = "left"><%=System.getProperty("os.name")%>-<%=System.getProperty("os.arch")%>-<%=System.getProperty("os.version")%></td>
	</tr>
	<tr>
		<td align = "right">Java Version: </td><td align = "left"><%=System.getProperty("java.version")%></td>
	</tr>
	<tr>
		<td align = "right">Java Vendor: </td><td align = "left"><a href = "<%=System.getProperty("java.vendor.url")%>"><%=System.getProperty("java.vendor")%></a></td>
	</tr>
	<tr>
		<td align = "right">Virtual Machine: </td><td align = "left"><%=System.getProperty("java.vm.name")%> <%=System.getProperty("java.vm.version")%></td>
	</tr>
	<tr>
		<td align = "right">Free Memory: </td><td align = "left"><%=Runtime.getRuntime().freeMemory()/1024%> KB</td>
	</tr>
	<tr>
		<td align = "right">Total Memory: </td><td align = "left"><%=Runtime.getRuntime().totalMemory()/1024%> KB</td>
	</tr>
	<tr>
		<td align = "right">Max Memory: </td><td align = "left"><%=Runtime.getRuntime().maxMemory()/1024%> KB</td>
	</tr>
	<tr>
		<td align = "right">Servlet Container: </td><td align = "left"><%=getServletContext().getServerInfo()%></td>
	</tr>
	<tr>
		<td align = "right">Servlet API: </td><td align = "left"><%=getServletContext().getMajorVersion()%>.<%=getServletContext().getMinorVersion()%></td>
	</tr>
</table>
