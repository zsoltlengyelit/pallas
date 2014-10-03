package io.pallas.core.execution;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * @author lzsolt
 */
public class Redirect implements Response {

    private final String location;

    /**
     * @param location
     *            location to redirect
     */
    public Redirect(final String location) {
        super();
        this.location = location;
    }

    @Override
    public void render(final HttpServletResponse response) {
        try {
            response.sendRedirect(location);
        } catch (final IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

}
