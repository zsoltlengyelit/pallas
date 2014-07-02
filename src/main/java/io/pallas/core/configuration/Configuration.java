package io.pallas.core.configuration;

/**
 * Configuration that specifies the comonents of application.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 *
 */
public interface Configuration {

    Object getValue(String path);

    String getString(String path);

    boolean getBoolean(String path);

}
