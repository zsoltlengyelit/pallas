package io.pallas.core.execution;

import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public interface Response extends Result {

	void render(HttpResponse response);

}
