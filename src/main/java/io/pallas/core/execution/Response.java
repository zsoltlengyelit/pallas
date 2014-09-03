package io.pallas.core.execution;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public interface Response {

    void render(HttpServletResponse response);

}
