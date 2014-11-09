package io.pallas.core.ws.events;

import io.pallas.core.ws.WsChannel;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class OnMessage extends AbstractWsEvent {

    private final String message;

    public OnMessage(final String message, final WsChannel channel) {
        super(channel);
        this.message = message;

    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

}
