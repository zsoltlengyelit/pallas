package io.pallas.core.asset;

import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.execution.Response;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.lf5.util.StreamUtils;

/**
 * @author lzsolt
 */
public class AssetResponse implements Response {

    private final Asset asset;

    /**
     * @param asset
     */
    public AssetResponse(final Asset asset) {
        super();
        this.asset = asset;
    }

    @Override
    public void render(final HttpServletResponse response) {

        //response.addHeader("Content-Disposition", "attachment; filename="+"sampleZip.zip");
        //response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        response.setContentType(asset.getContentType());

        try {
            final byte[] streamBytes = StreamUtils.getBytes(asset.getStream());

            response.setContentLength(streamBytes.length);
            final OutputStream responseOutputStream = response.getOutputStream();
            responseOutputStream.write(streamBytes);
            response.flushBuffer();

        } catch (final IOException exception) {
            throw new InternalServerErrorException(exception);
        }
    }

    public static AssetResponse notFoundAsset() {
        return new NotFoundAsset();
    }

    /**
     * HTTP 500 response
     * 
     * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
     */
    private static class NotFoundAsset extends AssetResponse {

        public NotFoundAsset() {
            super(null);
        }

        @Override
        public void render(final HttpServletResponse response) {
            try {

                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                response.flushBuffer();
            } catch (final IOException e) {
            }

        }

    }

}
