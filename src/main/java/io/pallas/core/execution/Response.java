package io.pallas.core.execution;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public interface Response extends Result {

    void render(HttpServletResponse response);

}
