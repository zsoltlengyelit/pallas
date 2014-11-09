package io.pallas.core.ws;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class WsChannel {

    private final Channel channel;

    /**
     * @param channel
     */
    public WsChannel(final Channel channel) {
        super();
        this.channel = channel;
    }

    public void write(final String string) {
        channel.write(new TextWebSocketFrame(string));
    }

    Channel getChannel() {
        return channel;
    }
}
