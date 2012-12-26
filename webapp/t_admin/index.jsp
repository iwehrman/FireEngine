<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
String param = com.labfire.fe.template.TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = context.getVirtualRoot() + servletName + '?' + param + '=';
%>
<a href="<%=pfix + "admin"%>">FireEngine</a> Control Panel
<ul>
	<li><a href = "<%=pfix + "admin_authentication_panel"%>">Users &amp; Organizations</a> - Create, edit, and delete users and organizations.</li>
	<li><a href = "<%=pfix + "admin_authorization_panel"%>">Protected Areas</a> - Create, edit and delete protected areas of the site.</li>
	<li><a href = "<%=pfix + "admin_prefs_panel"%>">System Prefs</a> - Manage default application settings</li>
	<li><a href = "<%=pfix + "admin_log_panel"%>">Log Messages</a> - View log messages.</li>
	<li><a href = "<%=pfix + "admin_connection_panel"%>">Database Connections</a> - View status of system database connection pools.</li>
	<li><a href = "<%=pfix + "admin_cache_panel"%>">Cache Status</a> - View and delete system cache entries.</li>
	<li><a href = "<%=pfix + "admin_cron_panel"%>">Cron Status</a> - View and delete system <acronym title = "Chronological system actions">cron</acronym> entries.</li>
	<li><a href = "<%=pfix + "admin_status"%>">Engine Status</a></li>
	<li><a href = "<%=pfix + "admin_about"%>">About FireEngine</a></li>
</ul>
