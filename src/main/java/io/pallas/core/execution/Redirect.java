package io.pallas.core.execution;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lzsolt
 *
 */
public class Redirect implements Response {

    @Override
    public void render(final HttpServletResponse response) {
        throw new UnsupportedOperationException();
    }

}
