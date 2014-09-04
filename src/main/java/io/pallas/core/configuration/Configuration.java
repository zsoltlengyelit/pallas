package io.pallas.core.configuration;

/**
 * Configuration that specifies the comonents of application.
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public interface Configuration {

    <T> T getValue(String path);

    <T> T getValue(String path, T defaultValue);

    String getString(String path, String defaultValue);

    String getString(String path);

    boolean getBoolean(String path);

    boolean getBoolean(String path, boolean defaultValue);

    public int getInt(final String path);

    public int getInt(final String path, int defaultValue);

}
