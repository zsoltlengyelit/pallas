package io.pallas.core.ws;

import io.pallas.core.util.Json;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

import java.util.Map.Entry;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Broadcaster {

    private final String path;
    private final WebSocketConnectionHandler connectionHandler;

    public Broadcaster(final String path, final WebSocketConnectionHandler connectionHandler) {
        this.path = path;
        this.connectionHandler = connectionHandler;

    }

    /**
     * Broadcasts Json message.
     *
     * @param data
     *            DTO
     */
    public void broadcastJson(final Object data) {

        final String message = Json.create().toJsonText(data);
        broadcast(message);
    }

    /**
     * Broadcasts message to channels on path
     *
     * @param message
     */
    public void broadcast(final String message) {

        for (final Entry<WebSocketChannel, String> entry : connectionHandler.getWebSocketChannels().entrySet()) {

            final String url = entry.getValue();

            // match path
            if (url.startsWith(path)) {
                final WebSocketChannel channel = entry.getKey();

                WebSockets.sendText(message, channel, null);
            }
        }

    }

    //    public void broadcastBinary(final byte[] data) {
    //
    //        final ByteBuffer buffer = ByteBuffer.wrap(data);
    //
    //        for (final Entry<WebSocketChannel, String> entry : connectionHandler.getWebSocketChannels().entrySet()) {
    //
    //            final String url = entry.getValue();
    //
    //            // match path
    //            if (url.startsWith(path)) {
    //                final WebSocketChannel channel = entry.getKey();
    //
    //                WebSockets.sendBinary(buffer, channel, null);
    //            }
    //        }
    //    }

}
