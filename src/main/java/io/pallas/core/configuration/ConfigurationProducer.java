package io.pallas.core.configuration;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.common.base.Optional;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ConfigurationProducer {

    public static final String VIEW_FILE_SUFFIX_CONF_PROPERTY = "view.fileSuffix";
    public static final String VIEW_PATH_CONF_PROPERTY        = "view.basePath";

    public static final String DEFAULT_VIEW_FILE_SUFFIX       = ".wdgt";
    public static final String DEFAULT_VIEW_PATH              = "/WEB-INF/view";

    @Inject
    private Configuration      configuration;

    @Produces
    @ConfProperty(VIEW_FILE_SUFFIX_CONF_PROPERTY)
    public String viewFileSuffix() {
        return Optional.fromNullable(configuration.getString(VIEW_FILE_SUFFIX_CONF_PROPERTY)).or(DEFAULT_VIEW_FILE_SUFFIX);
    }

    @Produces
    @ConfProperty(VIEW_PATH_CONF_PROPERTY)
    public String viewBasePath() {
        return Optional.fromNullable(configuration.getString(VIEW_PATH_CONF_PROPERTY)).or(DEFAULT_VIEW_PATH);
    }

}
