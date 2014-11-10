package io.pallas.core.view;

import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public interface ViewRenderer {

	/**
	 * @param view
	 *            view instance to render
	 * @param response
	 *            reponse
	 */
	void render(View view, HttpResponse response);

}
