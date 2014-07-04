package io.pallas.core.servlet;

import io.pallas.core.execution.ExecutionContext;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(loadOnStartup = 1, urlPatterns = "/*")
public class PallasServlet implements Servlet {

	@Inject
	private Provider<ExecutionContext> executionContextProvider;

	@Override
	public void init(ServletConfig config) throws ServletException {
	}

	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@SuppressWarnings("unused")
	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) req;
		HttpServletResponse httpServletResponse = (HttpServletResponse) res;

		ExecutionContext executionContext = executionContextProvider.get();
		executionContext.execute(httpServletRequest, httpServletResponse);
	}

	@Override
	public String getServletInfo() {
		return null;
	}

	@Override
	public void destroy() {

	}

}
