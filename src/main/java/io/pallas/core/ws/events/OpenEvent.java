package io.pallas.core.ws.events;

import io.pallas.core.ws.WsChannel;

import java.io.Serializable;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class OpenEvent implements Serializable {

    private final WsChannel channel;

    public OpenEvent(final WsChannel channel) {
        this.channel = channel;
    }

    /**
     * @return the channel
     */
    public WsChannel getChannel() {
        return channel;
    }

}
