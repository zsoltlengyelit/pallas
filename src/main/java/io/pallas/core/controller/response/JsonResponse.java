package io.pallas.core.controller.response;

import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.execution.Response;
import io.pallas.core.util.Json;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class JsonResponse implements Response {

    private final Object data;

    public JsonResponse(final Object data) {
        this.data = data;
    }

    @Override
    public void render(final HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON);
        final String content = Json.create().toJsonText(data);
        try {
            response.getWriter().append(content);
        } catch (final IOException exception) {
            throw new InternalServerErrorException(exception);
        }

    }

}
