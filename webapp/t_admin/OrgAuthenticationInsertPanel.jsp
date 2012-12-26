<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.common.*" %>
<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
String vRoot = context.getVirtualRoot();
String param = com.labfire.fe.template.TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = vRoot + servletName + '?' + param + '=';
%>
	Add Organization
	<form action = "<%=vRoot%>servlet/com.labfire.fe.auth.OrgAuthenticationEditServlet" method = "post">
		<table border = "0">
			<tr>
				<td align = "right">Full Name</td>
				<td align = "left"><input name = "orgName" type = "text" size="30"></input></td>
			</tr>
			<tr>
				<td align = "right">Short Name</td>
				<td align = "left"><input name = "orgShortName" type = "text" size="10"></input></td>
			</tr>
			<tr>
				<td align = "right">Website</td>
				<td align = "left"><input name = "orgURL" type = "text" size="30"></input></td>
			</tr>
			<tr>
				<td align = "right">Address</td>
				<td align = "left"><input name = "orgAddress1" type = "text" size="30"></input></td>
			</tr>
			<tr>
				<td align = "right">Address</td>
				<td align = "left"><input name = "orgAddress2" type = "text" size="30"></input></td>
			</tr>
			<tr>
				<td align = "right">City</td>
				<td align = "left"><input name = "orgCity" type = "text"></input></td>
			</tr>
			<tr>
				<td align = "right">State</td>
				<td align = "left"><input name = "orgState" type = "text" size="3"></input></td>
			</tr>
			<tr>
				<td align = "right">Zip</td>
				<td align = "left"><input name = "orgZip" type = "text" size="6"></input></td>
			</tr>
			<tr>
				<td align = "right">Country</td>
				<td align = "left"><input name = "orgCountry" type = "text"></input></td>
			</tr>
			<tr>
				<td align = "right">Phone</td>
				<td align = "left"><input name = "orgPhone" type = "text" size="12"></input></td>
			</tr>
			<tr>
				<td align = "right">Default Type</td>
				<td align = "left">
					<select name = "orgDefaultAID">
<%
for (int i = 1; i <= 3; i++) {
%>
						<option value = "<%=i%>"><%=AccessAuthenticationService.getAIDName(i)%></option>
<%
}
%>
					</select>
				</td>
			</tr>
		</table>
		<input type = "hidden" name = "orgID"  value = "new"></input>
		<input type = "hidden" name = "_redirect" value = "<%=pfix%>admin_authentication_panel"></input>
		<input type = "submit" value = "Add Organization">
	</form>
