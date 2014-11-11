package io.pallas.core.view;

import javax.servlet.http.HttpServletResponse;

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
    void render(View view, HttpServletResponse response);

}
