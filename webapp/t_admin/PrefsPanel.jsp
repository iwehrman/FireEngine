<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.prefs.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.labfire.fe.template.*" %>
	<table border = "0">
		<tr>
			<th>Key</th>
			<th>Value</th>
			<th>edit</td>
			<th>delete</th>
		<tr>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
String param = com.labfire.fe.template.TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = context.getVirtualRoot() + servletName + '?' + param + '=';
String k;
String v;
Map prefs = PrefService.getSystemPrefs();
List sortedKeys = new LinkedList(prefs.keySet());
Collections.sort(sortedKeys);
Iterator keys = sortedKeys.iterator();
	while(keys.hasNext()) {
		k = (String)keys.next();
		v = (String)prefs.get(k);
%>
		<tr>
			<td><%=k%></td>
			<td><% if (v != null) { if (v.length() > 50) { out.print(Strings.escapeHTML(v.substring(0,50)) + "..."); } else { out.print(Strings.escapeHTML(v)); } } %></td>
			<td><a href = "<%=pfix%>admin_prefs_edit_panel&key=<%=k%>">edit</a></td>
			<td><a href = "javascript:pref_del('<%=k%>');">delete</a></td>
		</tr>
<%
	}
%>
	</table><a href = "<%=pfix%>admin_prefs_insert_panel">Add New Pref</a>