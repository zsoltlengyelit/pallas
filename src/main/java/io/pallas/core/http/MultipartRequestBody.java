package io.pallas.core.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class MultipartRequestBody {

    private final Map<String, List<FileItem>> map;

    public MultipartRequestBody(final HttpRequest httpRequest, final Map<String, List<FileItem>> map) {
        this.map = map;
    }

    public Map<String, String[]> asFormUrlEncoded() {
        final Map<String, String[]> formData = new HashMap<String, String[]>();

        for (final String key : map.keySet()) {
            final List<FileItem> items = map.get(key);
            for (final FileItem fileItem : items) {

                if (fileItem.isFormField()) {
                    final String fieldName = fileItem.getFieldName();
                    final String content = fileItem.getString();
                    // TODO handle multivalue array

                    formData.put(fieldName, new String[] { content });
                }

            }
        }

        return formData;
    }
}
