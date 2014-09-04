package io.pallas.core.asset;

import io.pallas.core.annotations.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;

/**
 *
 * @author lzsolt
 *
 */
@Controller("asset")
public class AssetController {

    @Inject
    private AssetManager assetManager;

    /**
     *
     * @param filePath
     * @return
     */
    public Asset serve(@QueryParam("file") final String filePath) {

        final String absolutePath = assetManager.getAssetPath(filePath);
        final File file = new File(absolutePath);

        String contentType;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (final IOException e) {
            contentType = "plain/text";
        }
        if (null == contentType) {
            contentType = "plain/text";
        }

        return new Asset(file, contentType);
    }
}
