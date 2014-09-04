package io.pallas.core.view.wiidget;

import io.pallas.core.asset.AssetManager;

import javax.inject.Inject;

import com.landasource.wiidget.Tag;
import com.landasource.wiidget.Wiidget;

/**
 *
 * @author lzsolt
 *
 */
public class AbstractAsset extends Wiidget {

    private String src;

    private String type;

    @Inject
    private AssetManager assetManager;

    @Override
    public void run() {
        super.run();

        final String assetPath = assetManager.publishRelativeContextFile(src);

        // TODO script
        final String tag = new Tag("script").a("src", assetPath).a("type", type).toString();

        write(tag);
    }

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
