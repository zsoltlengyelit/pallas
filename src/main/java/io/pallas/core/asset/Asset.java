package io.pallas.core.asset;

import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.execution.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author lzsolt
 *
 */
public class Asset implements Response {

    private final File file;
    private final String mimeType;

    /**
     *
     * @param file
     *            file to write in response
     */
    public Asset(final File file, final String mimeType) {
        super();
        this.file = file;
        this.mimeType = mimeType;
    }

    @Override
    public void render(final HttpServletResponse response) {

        //response.addHeader("Content-Disposition", "attachment; filename="+"sampleZip.zip");
        //response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        response.setContentType(mimeType);
        response.setContentLength((int) file.length());
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            final OutputStream responseOutputStream = response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
            response.flushBuffer();

        } catch (final IOException exception) {
            throw new InternalServerErrorException(exception);
        } finally {
            // close file
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (final IOException exception) {
                    throw new InternalServerErrorException(exception);
                }
            }
        }
    }

}
