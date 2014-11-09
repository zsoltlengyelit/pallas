package io.pallas.core.ws.events;

import io.pallas.core.ws.WsChannel;

public class OnError extends AbstractWsEvent {

    private final Throwable throwable;

    public OnError(final Throwable throwable, final WsChannel channel) {
        super(channel);
        this.throwable = throwable;
    }

    /**
     * @return the throwable
     */
    public Throwable getCause() {
        return throwable;
    }

}
