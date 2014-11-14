package io.pallas.core.view.asset;

import io.pallas.core.asset.AssetManager;

import javax.inject.Inject;

import com.landasource.wiidget.Wiidget;

/**
 * @author lzsolt
 */
public abstract class AbstractAsset extends Wiidget {

    private String src;

    private String type;

    @Inject
    private AssetManager assetManager;

    @Override
    public void run() {
        super.run();

        final String assetPath = assetManager.publishRelativeContextFile(src, getContentType());

        writeTag(assetPath);
    }

    /**
     * By default the type tells the asset content type.
     *
     * @return content type with publish the asset
     */
    protected String getContentType() {
        return getType();
    }

    protected abstract void writeTag(String assetPath);

    public String getSrc() {
        return src;
    }

    public void setSrc(final String src) {
        this.src = src;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

}
