<%@ page import="com.labfire.fe.template.*" %>
<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%
Template t = TemplateService.select(request);
FireEngineContext context = Servlets.getFireEngineContext(application);
String vRoot = context.getVirtualRoot();
String param = TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = vRoot + servletName + '?' + param + '=';
%>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>Labfire | FireEngine | Admin</title>
	<meta name = "keywords" content = "Labfire, Labfire, Inc. Labfire Inc., Seamless Technical Solutions, Agile Internet Development, Seamless, Technical, Solutions, web application, webapp, network infrastructure, web, network, internet, www, world wide web, linux, unix, multiplatform, solutions for business, lab, fire" />
	<meta name = "description" content = "Labfire is an agile internet devlopment company. By analyzing entire workflow systems, we create seamless technical solutions to the problems businesses face. Labfire specializes in creating web-based applications that streamline business operations. Our capabilities also span the development of robust network infrastructures to realize the overall goal of efficient systems." />
	<style type = "text/css" media = "screen">@import "<%=vRoot%>fe_style.css";</style>
	<link rel="stylesheet" type="text/css" media="print" href="http://labfire.com/lf_print.css" />
	<link rel="stylesheet alternate" type="text/css" media="screen" title = "Friendly Fonts" href="http://labfire.com/lf_friendly.css" />
	<link rel="stylesheet alternate" type="text/css" media="screen" title = "Print Style" href="http://labfire.com/lf_print.css" />
	<style type = "text/css" media = "screen">
			.FINE {
				background-color: #cfc;
			}
			
			.INFO {
				background-color: #dff;
			}
			
			.WARNING {
				background-color: #feb;
			}
			
			.SEVERE {
				background-color: #faa;
			}
		</style>
		<script type="text/javascript">
		<!--
			function org_del(i,o) {
   				if (confirm("Really delete organization '" + o + "'?"))
      				document.location = "<%=vRoot%>servlet/com.labfire.fe.auth.OrgAuthenticationDeleteServlet?oid=" + i;
			}
			function user_del(i,o) {
   				if (confirm("Really delete user '" + o + "'?"))
      				document.location = "<%=vRoot%>servlet/com.labfire.fe.auth.AuthenticationDeleteServlet?id=" + i;
			}
			function pref_del(k) {
   				if (confirm("Really delete preference '" + k + "'?"))
      				document.location = "<%=vRoot%>servlet/com.labfire.fe.prefs.PrefDeleteServlet?key=" + k;
			}
		-->
	</script>
</head>
<body>
	<div class = "wrapper">
		<div class = "header">
			<div class = "headerleft">
				<div class = "headerimg">
					<a class = "imganchor" href = "/" title = "Labfire"><img class = "logo" src = "http://labfire.com/flame.png" alt = "Labfire" /></a>
				</div>
			</div>
			<div class = "headerright">
				 <a class = "imganchor" href = "/" title = "Seamless Technical Solutions"><img class = "logo" src = "http://labfire.com/fe_text.gif" alt = "Labfire" /></a>
			</div>
		</div>
		<div class = "menu">
			<div class = "menuleft">
				<span class = "menutext">
					<a href = "<%=vRoot + servletName%>" class = "menuanchor">Home</a> | 
					<a href = "http://labfire.com/" class = "menuanchor">Labfire</a> | 
					<a href = "http://labfire.com/about/" class = "menuanchor">About</a> | 
					<a href = "http://firefly.labfire.com/firefly/" class = "menuanchor">Firefly</a> | 
					<a href = "<%=pfix + "admin"%>" class = "menuanchor"><b>FireEngine</b></a> | 
					<a href = "http://labfire.com/contact/" class = "menuanchor">Contact</a>
				</span>
			</div>
			<div class = "menuright">
				<span class = "menufloattext">Seamless Technical Solutions</span>
			</div>
		</div>
		<div class = "nav">
			<div class = "navleft">
				<span class = "navtext">
					<a href = "<%=pfix + "admin"%>" class = "navanchor"><b>Index</b></a> | 
					<a href = "<%=pfix + "admin_authentication_panel"%>" class = "navanchor">Users</a> | 
					<a href = "<%=pfix + "admin_authorization_panel"%>" class = "navanchor">Constraints</a> | 
					<a href = "<%=pfix + "admin_prefs_panel"%>" class = "navanchor">Prefs</a> | 
					<a href = "<%=pfix + "admin_log_panel"%>" class = "navanchor">Log</a> | 
					<a href = "<%=pfix + "admin_cache_panel"%>" class = "navanchor">Cache</a> | 
					<a href = "<%=pfix + "admin_connection_panel"%>" class = "navanchor">Db</a> | 
					<a href = "<%=pfix + "admin_cron_panel"%>" class = "navanchor">Cron</a> | 
					<a href = "<%=pfix + "admin_about"%>" class = "navanchor">About</a>
				</span>
			</div>
			<div class = "navright">
				<span class = "navfloattext">For Business</span>
			</div>
		</div>
 	</div>
	
	<div class = "content" id="content">
		<div class="story">
			<%
			String file = ((Block)t.getBlocks().get("main")).getFile();
			boolean next = (t.getBlocks().get("secondary") != null);
			%>
			<jsp:include page="<%=file%>" flush="true" />
		</div>
			<%
			if (next) {
				file = ((Block)t.getBlocks().get("secondary")).getFile();
			%>
		<div class="story">
			<jsp:include page="<%=file%>" flush="true" />
		</div>
	</div>
		<% } %>
</body>
</html>
