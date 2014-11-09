package io.pallas.core.ws.events;

import io.pallas.core.ws.WsChannel;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class AbstractWsEvent {

    private final WsChannel channel;

    public AbstractWsEvent(final WsChannel channel) {
        this.channel = channel;
    }

    /**
     * @return the channel
     */
    public WsChannel getChannel() {
        return channel;
    }
}
