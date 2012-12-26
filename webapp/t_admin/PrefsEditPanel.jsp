<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.prefs.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%
String key = request.getParameter("key");
String value = PrefService.getSystemPref(key, null);
AuthToken at = Servlets.getAuthToken(request);
FireEngineContext context = Servlets.getFireEngineContext(application);
String vRoot = context.getVirtualRoot();
String param = com.labfire.fe.template.TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = vRoot + servletName + '?' + param + '=';
%>
	Edit System Pref
	<form action = "<%=vRoot%>servlet/com.labfire.fe.prefs.PrefEditServlet" method = "post">
		<table border = "0">
			<tr>
				<td align = "right">Key</td>
				<td align = "left"><code><%=key%></code><input type="hidden" name="key" value="<%=key%>"></input></td>
			</tr>
			<tr>
				<td align = "right">Value</td>
				<td align = "left"><textarea name="value" rows="15" cols="60"><% if (value != null) { out.println(value); } %></textarea></td>
			</tr>
		</table>
		<input type = "hidden" name = "_redirect" value = "<%=pfix%>admin_prefs_panel"></input>
		<input type = "hidden" name = "_required" value = "key,value"></input>
		<input type = "submit" value = "Update">
	</form>
