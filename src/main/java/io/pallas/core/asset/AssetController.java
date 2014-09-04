package io.pallas.core.asset;

import io.pallas.core.annotations.Controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.QueryParam;

/**
 * @author lzsolt
 */
@Controller("asset")
public class AssetController {

    @Inject
    private AssetManager assetManager;
    @Inject
    private ServletContext context;

    /**
     * @param assetKey
     * @return
     */
    public AssetResponse serve(@QueryParam("file") final String assetKey) {

        final Asset asset = assetManager.getAsset(assetKey);

        if (null == asset) {
            return AssetResponse.notFoundAsset();
        }

        return new AssetResponse(asset);
    }

    public AssetResponse serveStatic(@QueryParam("file") final String file) {

        try {
            final String realPath = context.getRealPath("/" + file);

            final String contentType = calculateContentType(file);

            return new AssetResponse(new Asset(new FileInputStream(realPath), contentType));
        } catch (final FileNotFoundException | NullPointerException e) {
            return AssetResponse.notFoundAsset();
        }
    }

    private String calculateContentType(final String file) {

        final String[] parts = file.split("\\.");
        final String ext = parts[parts.length - 1];

        final Map<String, String> mimeMap = new HashMap<String, String>();

        mimeMap.put("js", "application/javascript");
        mimeMap.put("css", "text/css");

        final String mime = mimeMap.get(ext);
        return mime == null ? "text/plain" : mime;
    }
}
