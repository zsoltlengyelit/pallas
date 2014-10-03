package io.pallas.core.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonGenerator.Feature;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Json {

    private final ObjectMapper defaultObjectMapper = new ObjectMapper();
    private volatile ObjectMapper objectMapper = null;

    /**
     * Static getter.
     *
     * @return Json instance
     */
    public static Json create() {
        return new Json();
    }

    /**
     * Get the ObjectMapper used to serialize and deserialize objects to and
     * from JSON values. This can be set to a custom implementation using
     * Json.setObjectMapper.
     *
     * @return the ObjectMapper currently being used
     */
    public ObjectMapper mapper() {
        if (objectMapper == null) {
            return defaultObjectMapper;
        } else {
            return objectMapper;
        }
    }

    private String generateJson(final Object o, final boolean prettyPrint, final boolean escapeNonASCII) {
        try {
            final StringWriter sw = new StringWriter();
            final JsonGenerator jgen = new JsonFactory(mapper()).createJsonGenerator(sw);
            if (prettyPrint) {
                jgen.setPrettyPrinter(new DefaultPrettyPrinter());
            }
            if (escapeNonASCII) {
                jgen.enable(Feature.ESCAPE_NON_ASCII);
            }
            mapper().writeValue(jgen, o);
            sw.flush();
            return sw.toString();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert an object to JsonNode.
     *
     * @param data
     *            Value to convert in Json.
     */
    public JsonNode toJson(final Object data) {
        try {
            return mapper().valueToTree(data);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert a JsonNode to a Java value
     *
     * @param json
     *            Json value to convert.
     * @param clazz
     *            Expected Java value type.
     */
    public <A> A fromJson(final JsonNode json, final Class<A> clazz) {
        try {
            return mapper().treeToValue(json, clazz);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new empty ObjectNode.
     */
    public ObjectNode newObject() {
        return mapper().createObjectNode();
    }

    /**
     * Creates a new empty ArrayNode.
     */
    public ArrayNode newArray() {
        return mapper().createArrayNode();
    }

    /**
     * Convert a JsonNode to its string representation.
     */
    public String stringify(final JsonNode json) {
        return generateJson(json, false, false);
    }

    /**
     * Convert a JsonNode to its string representation, escaping non-ascii
     * characters.
     */
    public String asciiStringify(final JsonNode json) {
        return generateJson(json, false, true);
    }

    /**
     * Convert a JsonNode to its string representation.
     */
    public String prettyPrint(final JsonNode json) {
        return generateJson(json, true, false);
    }

    /**
     * Parse a String representing a json, and return it as a JsonNode.
     */
    public JsonNode parse(final String src) {
        try {
            return mapper().readTree(src);
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Parse a InputStream representing a json, and return it as a JsonNode.
     */
    public JsonNode parse(final java.io.InputStream src) {
        try {
            return mapper().readTree(src);
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Parse a byte array representing a json, and return it as a JsonNode.
     */
    public JsonNode parse(final byte[] src) {
        try {
            return mapper().readTree(src);
        } catch (final Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * Inject the object mapper to use. This is intended to be used when Play
     * starts up. By default, Play will inject its own object mapper here, but
     * this mapper can be overridden either by a custom plugin or from
     * Global.onStart.
     */
    public void with(final ObjectMapper mapper) {
        objectMapper = mapper;
    }

    public Map<String, String> toMap(final JsonNode data) {

        final TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
        };
        try {
            return mapper().readValue(data, typeRef);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
