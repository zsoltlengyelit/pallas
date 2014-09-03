package io.pallas.core.view.wiidget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import com.landasource.wiidget.engine.configuration.ClassPathFileLoader;
import com.landasource.wiidget.io.FileLoader;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class ServletFileLoader implements FileLoader {

    private final ServletContext context;
    private final ClassPathFileLoader classPathFileLoader = new ClassPathFileLoader();;

    @Inject
    public ServletFileLoader(final ServletContext context) {
        this.context = context;
    }

    /*
     * (non-Javadoc)
     * @see com.landasource.wiidget.io.FileLoader#getFile(java.lang.String)
     */
    @Override
    public InputStream getFile(final String filename) {

        final String fullPath = getContextPath(filename);

        try {
            return new FileInputStream(fullPath);
        } catch (final FileNotFoundException e) {
            return classPathFileLoader.getFile(filename);
        }
    }

    private String getContextPath(final String filename) {
        return context.getRealPath("/WEB-INF/view/" + filename);
    }

    @Override
    public boolean exists(final String filename) {

        final String fullPath = getContextPath(filename);

        final boolean isFile = new File(fullPath).isFile();
        return isFile ? isFile : classPathFileLoader.exists(fullPath);
    }

}
