package io.pallas.core.routing;

import io.pallas.core.asset.AssetManager;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@RewriteConfiguration
public class Router extends AbstractRoutingProvider {

    @Inject
    private AssetManager assetManager;

    @Override
    protected void setRules(final ConfigurationBuilder builder, final ServletContext context) {

        final String urlPath = assetManager.getUrlPath();

        builder.addRule(Join.path(urlPath + "/{file}").to("/asset/serve?file={file}")).where("file").matches(".*");
        builder.addRule(Join.path("/static/{file}").to("/asset/serveStatic?file={file}")).where("file").matches(".*");

        // serve files
        //        final String pathname = context.getRealPath("/") + "/{file}";
        //        final File resource = new File(pathname.replace("\\", "/"));
        //        builder.addRule().when(Path.matches("/{file}").and(Filesystem.fileExists(resource)).and(Direction.isInbound()))
        //        .perform(Stream.from(resource).and(Response.setStatus(200)).and(Response.complete())).where("file").matches(".*");

    }

    // private Stream serve(resource, )

}
