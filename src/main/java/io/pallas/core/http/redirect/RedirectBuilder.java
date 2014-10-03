package io.pallas.core.http.redirect;

import io.pallas.core.controller.ActionReference;
import io.pallas.core.execution.Redirect;
import io.pallas.core.routing.LinkBuilder;

import java.util.List;

import javax.inject.Inject;

/**
 * Builds {@link Redirect}s in application context.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class RedirectBuilder {

    @Inject
    private LinkBuilder links;

    /**
     * @param location
     *            redirect to this location
     * @return
     */
    public Redirect to(final String location) {
        return new Redirect(location);
    }

    /**
     * @param appPath
     * @see ActionReference
     * @return redirect
     */
    public Redirect to(final Object[] appPath) {
        return to(ActionReference.of(appPath));
    }

    /**
     * @param appPath
     * @see ActionReference
     * @return redirect
     */
    public Redirect to(final List<?> appPath) {
        return to(ActionReference.of(appPath));
    }

    public Redirect to(final ActionReference reference) {
        return to(links.of(reference));
    }

}
