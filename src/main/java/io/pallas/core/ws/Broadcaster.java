package io.pallas.core.ws;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Broadcaster {

    private final String path;
    private final UrlMapperGroup group;

    public Broadcaster(final String path, final UrlMapperGroup group) {
        this.path = path;
        this.group = group;
    }

    /**
     * Broadcasts message to channels on path
     * 
     * @param message
     */
    public void broadcast(final String message) {

        final ChannelGroup subGroup = group.getChannels(path);
        subGroup.write(new TextWebSocketFrame(message));
    }

}
