package io.pallas.core.asset;

import io.pallas.core.annotations.Component;
import io.pallas.core.annotations.Configured;
import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.util.Hashids;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Responsible to put files to configured path.
 *
 * @author lzsolt
 */
@ApplicationScoped
@Component(AssetManager.COMPONENT_NAME)
public class AssetManager {

	public static final String COMPONENT_NAME = "assetManager";

	public static final String DEFAULT_URL_PATH = "/assets";

	private final Map<String, String> assetContents = new HashMap<>(); // this contents must be served as content
	private final Map<String, String> assetFiles = new HashMap<>();
	private final Map<String, String> mimeTypes = new HashMap<>(); // assetKey -> mime type(content type)

	//    @Inject
	//    private ServletContext servletContext;

	@Inject
	@Configured(defaultValue = DEFAULT_URL_PATH)
	private String urlPath;

	/**
	 * Published file where path is defined relative to deployment path
	 *
	 * @param relativePath
	 * @param contentType
	 * @return
	 */
	public String publishRelativeContextFile(final String relativePath, final String contentType) {
		final String realPath = relativePath;//servletContext.getRealPath(relativePath);
		if (null == realPath) {
			throw new InternalServerErrorException(String.format("File does not exists: %s", relativePath));
		}
		return publishRelative(new File(realPath), contentType);
	}

	/**
	 * @param inlineContent
	 * @return
	 */
	public String publishRelativeContent(final InlineAssetContent inlineContent) {

		final String parentHash = hashParent(inlineContent.getParent());

		final String assetName = parentHash + "/" + inlineContent.getName();
		assetContents.put(assetName, inlineContent.getContent());
		mimeTypes.put(assetName, inlineContent.getContentType());

		return generateRelativePath(assetName);
	}

	/**
	 * @param internalFile
	 *            file to publish
	 * @param contentType
	 * @return the published file relative path in URL (of context)
	 */
	public String publishRelative(final File internalFile, final String contentType) {

		final String parentHash = hashParent(internalFile.getParent());
		final String name = internalFile.getName();

		final String assetName = parentHash + "/" + name;
		assetFiles.put(assetName, internalFile.getAbsolutePath());
		mimeTypes.put(assetName, contentType);

		return generateRelativePath(assetName);
	}

	private String generateRelativePath(final String assetName) {
		return /* TODO servletContext.getContextPath() + */urlPath + "/" + assetName;
	}

	/**
	 * @param assetUrl
	 * @return the file when set, otherwise null
	 */
	public Asset getAsset(final String assetKey) {
		InputStream stream = null;

		final String absolutePath = assetFiles.get(assetKey);

		if (null == absolutePath) {
			final String content = assetContents.get(assetKey);
			if (null == content) {
				return null;
			} else {
				stream = new ByteArrayInputStream(content.getBytes());
			}
		} else {

			// handle file
			try {
				stream = new FileInputStream(absolutePath);
			} catch (FileNotFoundException | NullPointerException e) {
				return null;
			}
		}

		return new Asset(stream, mimeTypes.get(assetKey));
	}

	protected String hashParent(final String internalParentName) {

		final CRC32 crc32 = new CRC32();
		final byte[] bytes = internalParentName.getBytes();
		crc32.update(bytes, 0, bytes.length);

		try {
			return new Hashids().encrypt(crc32.getValue());
		} catch (final Exception e) {
			throw new InternalServerErrorException(e);
		}
	}

	/**
	 * @return the urlPath
	 */
	public String getUrlPath() {
		return urlPath;
	}

}
