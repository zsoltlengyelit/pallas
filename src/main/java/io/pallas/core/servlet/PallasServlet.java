package io.pallas.core.servlet;

import io.pallas.core.execution.ExecutionContext;

import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.weld.bean.builtin.ee.ServletContextBean;
import org.jboss.weld.context.bound.BoundRequestContext;

public class PallasServlet implements Servlet {

	@Inject
	private Provider<ExecutionContext> executionContextProvider;

	@Inject
	private BoundRequestContext requestContext;

	@Override
	public void init(final ServletConfig config) throws ServletException {

	}

	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
		final HashMap<String, Object> context = new HashMap<String, Object>();

		try {
			ServletContextBean.setServletContext(req.getServletContext());
			requestContext.associate(context);
			requestContext.activate();

			final HttpServletRequest httpServletRequest = (HttpServletRequest) req;
			final HttpServletResponse httpServletResponse = (HttpServletResponse) res;

			final ExecutionContext executionContext = executionContextProvider.get();
			executionContext.execute(httpServletRequest, httpServletResponse);

		} finally {
			try {
				requestContext.invalidate();
				requestContext.deactivate();
			} finally {
				requestContext.dissociate(context);
			}

			ServletContextBean.cleanup();
		}
	}

	@Override
	public String getServletInfo() {
		return null;
	}

	@Override
	public void destroy() {
	}

}
