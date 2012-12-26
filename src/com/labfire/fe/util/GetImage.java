package com.labfire.fe.util;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labfire.fe.FireEngineContext;
import com.labfire.fe.log.LogService;

/**
 @author <a href = "mailto:ian@labfire.com">Ian Wehrman</a>
 @see com.labfire.fe.auth.AuthenticationFilter
 @see com.labfire.fe.auth.AuthenticationService
 */


public class GetImage extends HttpServlet {
	private String blank;
	private FireEngineContext context;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		context = Servlets.getFireEngineContext(getServletContext());
		blank = context.getProperty("blank");
		if (blank == null) {
			LogService.logWarn("Unable to set blank image");
		}
	}

	/**
	 * doGet
	 * 
	 * @param request HttpServletRequest object associated with a request
	 * @param response HttpServletResponse object associated with a request
	 * @throws IOException - if an input or output error is detected when the servlet handles the GET request
	 * @throws ServletException - if the request for the GET could not be handled
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher rd;
		try {
			String src = request.getParameter("src");
			if (src != null 
					&& src.length() > 0 
					&& getServletContext().getResource(src) != null) {
				rd = request.getRequestDispatcher(src);
				if (rd != null) {
					rd.forward(request, response);
				} else {
					LogService.logError("Cannot acquire RequestDispatcher for " + request.getParameter("src"));
				}
			} else {
				rd = request.getRequestDispatcher(blank);
				if (rd != null) {
					rd.forward(request, response);
				} else {
					LogService.logError("Cannot acquire RequestDispatcher for " + blank);
				}
			}
		} catch (Exception e) {
			LogService.logError("Unable to get image", e);
		}
	}

	/**
		@param request HttpServletRequest object associated with a request
		@param response HttpServletResponse object associated with a request
		@throws IOException - if an input or output error is detected when the servlet handles the POST request
		@throws ServletException - if the request for the POST could not be handled
	*/
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);		
	}
}
