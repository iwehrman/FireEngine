<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.common.*" %>
<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%
Organization o = (Organization)request.getAttribute(OrganizationFactory.class.getName());
FireEngineContext context = Servlets.getFireEngineContext(application);
String vRoot = context.getVirtualRoot();
String param = com.labfire.fe.template.TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = vRoot + servletName + '?' + param + '=';
%>
	Edit Organization
	<form action = "<%=vRoot%>servlet/com.labfire.fe.auth.OrgAuthenticationEditServlet" method = "post">
		<table border = "0">
			<tr>
				<td align = "right">Full Name</td>
				<td align = "left"><input name = "orgName" type = "text" value = "<%=o.getOrgName()%>" size="30"></input></td>
			</tr>
			<tr>
				<td align = "right">Short Name</td>
				<td align = "left"><input name = "orgShortName" type = "text" value = "<%=o.getOrgShortName()%>" size="10"></input></td>
			</tr>
			<tr>
				<td align = "right">Website</td>
				<td align = "left"><input name = "orgURL" type = "text" value = "<%=o.getOrgURL()%>" size="30"></input></td>
			</tr>
			<tr>
				<td align = "right">Address</td>
				<td align = "left"><input name = "orgAddress1" type = "text" value = "<%=o.getOrgAddress1()%>" size="30"></input></td>
			</tr>
			<tr>
				<td align = "right">Address</td>
				<td align = "left"><input name = "orgAddress2" type = "text" value = "<%=o.getOrgAddress2()%>" size="30"></input></td>
			</tr>
			<tr>
				<td align = "right">City</td>
				<td align = "left"><input name = "orgCity" type = "text" value = "<%=o.getOrgCity()%>"></input></td>
			</tr>
			<tr>
				<td align = "right">State</td>
				<td align = "left"><input name = "orgState" type = "text" value = "<%=o.getOrgState()%>" size="3"></input></td>
			</tr>
			<tr>
				<td align = "right">Zip</td>
				<td align = "left"><input name = "orgZip" type = "text" value = "<%=o.getOrgZip()%>" size="6"></input></td>
			</tr>
			<tr>
				<td align = "right">Country</td>
				<td align = "left"><input name = "orgCountry" type = "text" value = "<%=o.getOrgCountry()%>"></input></td>
			</tr>
			<tr>
				<td align = "right">Phone</td>
				<td align = "left"><input name = "orgPhone" type = "text" value = "<%=o.getOrgPhone()%>" size="12"></input></td>
			</tr>
			<tr>
				<td align = "right">Default Type</td>
				<td align = "left">
					<select name = "orgDefaultAID">
<%
for (int i = 1; i <= 3; i++) {
%>
						<option value = "<%=i%>" <% if ((i == o.getOrgDefaultAID())) { out.print("selected = \"selected\""); } %>><%=AccessAuthenticationService.getAIDName(i)%></option>
<%
}
%>
					</select>
				</td>
			</tr>
		</table>
		<input type = "hidden" name = "orgID"  value = "<%=o.getOrgID()%>"></input>
		<input type = "hidden" name = "_redirect" value = "<%=pfix%>admin_authentication_panel"></input>
		<input type = "submit" value = "Update Organization">
	</form>
