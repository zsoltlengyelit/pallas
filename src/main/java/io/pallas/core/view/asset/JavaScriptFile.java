package io.pallas.core.view.asset;

import com.landasource.wiidget.Tag;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class JavaScriptFile extends AbstractAsset {

    public JavaScriptFile() {
        super();
        setType("application/javascript");// set default type
    }

    @Override
    protected void writeTag(final String assetPath) {
        final Tag tag = new Tag("script").a("src", assetPath).a("type", getType());
        write(tag);
    }
}
