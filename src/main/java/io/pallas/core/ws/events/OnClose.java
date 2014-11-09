package io.pallas.core.ws.events;

import io.pallas.core.ws.WsChannel;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class OnClose extends AbstractWsEvent {

    public OnClose(final WsChannel channel) {
        super(channel);
    }

}
