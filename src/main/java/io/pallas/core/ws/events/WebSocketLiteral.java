package io.pallas.core.ws.events;

import javax.enterprise.util.AnnotationLiteral;

public class WebSocketLiteral extends AnnotationLiteral<WebSocket> implements WebSocket {

    /**
     *
     */
    private static final long serialVersionUID = 1791065108036026684L;
    private final String path;

    public WebSocketLiteral(final String path) {
        this.path = path;
    }

    @Override
    public String path() {
        return path;
    }

}
