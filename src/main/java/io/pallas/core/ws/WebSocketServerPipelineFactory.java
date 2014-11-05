package io.pallas.core.ws;

import static org.jboss.netty.channel.Channels.pipeline;

import javax.inject.Inject;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class WebSocketServerPipelineFactory implements ChannelPipelineFactory {

    @Inject
    private WebSocketServerHandler handler;

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        final ChannelPipeline pipeline = pipeline();

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("handler", handler);
        return pipeline;
    }
}