package io.pallas.core.view.wiidget;

import io.pallas.core.execution.Response;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.landasource.wiidget.Renderer;
import com.landasource.wiidget.engine.Engine;

/**
 *
 * @author lzsolt
 *
 */
public abstract class WiidgetPage extends com.landasource.wiidget.WiidgetView implements Response{

	@Inject
	private Engine engine;

	public WiidgetPage() {
		super(null);
	}

	@Override
	public void init() {
		super.init();

		getWiidgetContext().set("this", this);
	}

	@Override
	public void render(HttpServletResponse response) {
		try {

			final String result = Renderer.create(getEngine()).render(this);

			response.setHeader("Content-Type", MediaType.TEXT_HTML);
			response.getWriter().append(result);

		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Engine getEngine() {
		return engine;
	}

}
