package io.pallas.core.asset;

import io.pallas.core.configuration.ConfProperty;
import io.pallas.core.configuration.Configuration;
import io.pallas.core.execution.InternalServerErrorException;
import io.pallas.core.util.Hashids;

import java.io.File;
import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Responsible to put files to configured path.
 *
 * @author lzsolt
 *
 */
@RequestScoped
public class AssetManager {

    public static final String DEFAULT_ASSETS_FOLDER = "/assets";
    public static final String ASSETS_FOLDER_CONF_PROPERTY = "assets.folder";

    @Inject
    private Logger logger;

    @Inject
    private ServletContext servletContext;

    @Inject
    private Configuration configuration;

    /**
     * Published file where path is defined relative to deployment path
     *
     * @param relativePath
     * @return
     */
    public String publishRelativeContextFile(final String relativePath) {
        final String realPath = servletContext.getRealPath(relativePath);
        if (null == realPath) {
            throw new InternalServerErrorException(String.format("File does not exists: %s", relativePath));
        }
        return publishRelative(new File(realPath));
    }

    public String getAssetPath(final String assetRelativepath) {
        return produceAssetsFolder() + "/" + assetRelativepath;
    }

    /**
     *
     * @param internalFile
     *            file to publish
     * @return the published file relative path in URL (of context)
     */
    public String publishRelative(final File internalFile) {

        final File assetFolder = new File(produceAssetsFolder());

        try {
            // create asset folder
            FileUtils.forceMkdir(assetFolder);

            // create folder of asset
            final File publishFolder = createAssetParentFolder(internalFile, assetFolder);

            final File publishedFile = new File(publishFolder, internalFile.getName());

            boolean mustPublish = !publishedFile.isFile();

            if (!mustPublish) {
                mustPublish = publishedFile.lastModified() != internalFile.lastModified();
                logger.info("Republish file: " + internalFile.getAbsolutePath());
            }

            if (mustPublish) { // copy only when does not exists

                logger.debug(String.format("Publish file: %s", publishedFile.getAbsolutePath()));

                FileUtils.copyFileToDirectory(internalFile, publishFolder, true); // real publish
            }

            final String contextPath = servletContext.getContextPath();

            return contextPath + getAssetFolderName() + "/" + publishFolder.getName() + "/" + internalFile.getName();

        } catch (final IOException exception) {
            logger.error(String.format("Cannot publish file: %s", internalFile.getAbsolutePath()));
            throw new InternalServerErrorException(exception);
        }
    }

    /**
     *
     * @return
     */
    @Produces
    @ConfProperty(ASSETS_FOLDER_CONF_PROPERTY)
    public String produceAssetsFolder() {
        final String assetFolderPath = getAssetFolderName();
        //        final String deploymentFolder = servletContext.getRealPath(assetFolderPath);

        //final File assetFolder = new File(deploymentFolder);
        //return assetFolder.getAbsolutePath();

        final File tmpDir = (File) servletContext.getAttribute(ServletContext.TEMPDIR);

        final File assetsFolder = new File(tmpDir + assetFolderPath);
        return assetsFolder.getAbsolutePath();

    }

    private String getAssetFolderName() {
        final String assetFolderPath = configuration.getString(ASSETS_FOLDER_CONF_PROPERTY, DEFAULT_ASSETS_FOLDER);
        return assetFolderPath;
    }

    protected File createAssetParentFolder(final File internalFile, final File assetFolder) throws IOException {
        final String internalParentName = internalFile.getParentFile().getAbsolutePath();
        final String assetParent = hashParent(internalParentName);
        final File publishFolder = new File(assetFolder.getAbsolutePath(), assetParent);
        FileUtils.forceMkdir(publishFolder);

        return publishFolder;
    }

    protected String hashParent(final String internalParentName) {

        try {
            final String encrypt = new Hashids().encrypt(internalParentName);
            if (encrypt.length() > 200) {
                return encrypt.substring(0, 200);
            }
            return encrypt;
        } catch (final Exception e) {
            throw new InternalServerErrorException(e);
        }
    }

}
