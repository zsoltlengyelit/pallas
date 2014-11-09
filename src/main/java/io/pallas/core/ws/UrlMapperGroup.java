package io.pallas.core.ws;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

@ApplicationScoped
class UrlMapperGroup extends DefaultChannelGroup {

    private final Map<Integer, String> channelUrl = new HashMap<Integer, String>();

    void put(final String url, final Channel channel) {
        add(channel);
        channelUrl.put(channel.getId(), url);
    }

    @Override
    public boolean remove(final Object o) {
        final Channel channel = (Channel) o;
        channelUrl.remove(channel);
        return super.remove(o);
    }

    public String getUrl(final Channel channel) {
        return channelUrl.get(channel.getId());
    }

    /**
     * Get channels for specified path.
     *
     * @param path
     *            URL path
     * @return
     */
    public ChannelGroup getChannels(final String path) {

        final ChannelGroup subGroup = new DefaultChannelGroup(path);

        for (final Channel channel : this) {
            final String url = getUrl(channel);

            if (path.equals(url)) {
                subGroup.add(channel);
            }
        }

        return subGroup;
    }

}
