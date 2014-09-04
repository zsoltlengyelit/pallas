package io.pallas.core.view.wiidget.asset;

import com.landasource.wiidget.Tag;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class StyleSheetFile extends AbstractAsset {

    private String rel = "stylesheet";

    private String media = "all";

    public StyleSheetFile() {
        super();
        setType("text/css");// set default type
    }

    @Override
    protected void writeTag(final String assetPath) {
        final Tag tag = new Tag("script").a("src", assetPath).a("type", getType()).a("rel", getRel()).a("media", getMedia());
        write(tag);
    }

    /**
     * @return the rel
     */
    public String getRel() {
        return rel;
    }

    /**
     * @param rel
     *            the rel to set
     */
    public void setRel(final String rel) {
        this.rel = rel;
    }

    /**
     * @return the media
     */
    public String getMedia() {
        return media;
    }

    /**
     * @param media
     *            the media to set
     */
    public void setMedia(final String media) {
        this.media = media;
    }

}
