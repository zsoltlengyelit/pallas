package io.pallas.core.view.form;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * A form validation error.
 */
public class ValidationError {

    private final String key;
    private final List<String> messages;
    private final List<Object> arguments;

    /**
     * Constructs a new <code>ValidationError</code>.
     *
     * @param key
     *            the error key
     * @param message
     *            the error message
     */
    public ValidationError(final String key, final String message) {
        this(key, message, ImmutableList.of());
    }

    /**
     * Constructs a new <code>ValidationError</code>.
     *
     * @param key
     *            the error key
     * @param message
     *            the error message
     * @param arguments
     *            the error message arguments
     */
    public ValidationError(final String key, final String message, final List<Object> arguments) {
        this.key = key;
        this.arguments = arguments;
        this.messages = ImmutableList.of(message);
    }

    /**
     * Constructs a new <code>ValidationError</code>.
     *
     * @param key
     *            the error key
     * @param messages
     *            the list of error messages
     * @param arguments
     *            the error message arguments
     */
    public ValidationError(final String key, final List<String> messages, final List<Object> arguments) {
        this.key = key;
        this.messages = messages;
        this.arguments = arguments;
    }

    /**
     * Returns the error key.
     */
    public String key() {
        return key;
    }

    /**
     * Returns the error message.
     */
    public String message() {
        return messages.get(messages.size() - 1);
    }

    /**
     * Returns the error messages.
     */
    public List<String> messages() {
        return messages;
    }

    /**
     * Returns the error arguments.
     */
    public List<Object> arguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "ValidationError(" + key + "," + messages + "," + arguments + ")";
    }

}