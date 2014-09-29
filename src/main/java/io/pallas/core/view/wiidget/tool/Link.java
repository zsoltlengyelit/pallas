package io.pallas.core.view.wiidget.tool;

import io.pallas.core.routing.LinkBuilder;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.landasource.wiidget.Tag;
import com.landasource.wiidget.annotation.DefaultField;
import com.landasource.wiidget.library.html.HtmlTagWiidget;
import com.landasource.wiidget.parser.resource.ClassWiidgetResource;
import com.landasource.wiidget.util.Strings;

/**
 * Useges:
 * <ul>
 * <li>Link("simple/url")</li>
 * <li>Link(ControllerClass)</li>
 * <li>Link([ControllerClass])</li>
 * <li>Link([ControllerClass, "actionName"])</li>
 * <li>Link([ControllerClass, "actionName", {param : "value"}])</li>
 * </ul>
 *
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
public class Link extends HtmlTagWiidget {

    /**
     * Can be: {@link ClassWiidgetResource}, {@link String}, {@link List}
     *
     * @see LinkBuilder
     */
    @DefaultField
    private Object to;

    /**
     * Label.
     */
    private String label;

    private final LinkBuilder builder;

    /**
     * @param builder
     */
    @Inject
    public Link(final LinkBuilder builder) {
        super();
        this.builder = builder;
    }

    @Override
    public void init() {
        startBuffer();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void run() {
        final String buffer = endBuffer();

        final String href;

        if (to instanceof List) {
            final List param = (List) to;

            switch (param.size()) {
            case 1:
                href = builder.of((ClassWiidgetResource) param.get(0));
                break;
            case 2:
                href = builder.of((ClassWiidgetResource) param.get(0), (String) param.get(1));
                break;

            case 3:
                href = builder.of((ClassWiidgetResource) param.get(0), (String) param.get(1), (Map<String, Object>) param.get(2));
                break;
            default:
                throw new IllegalArgumentException("Illegal link: " + param);
            }

        } else if (to instanceof ClassWiidgetResource) {

            final Class<?> wiidgetClass = ((ClassWiidgetResource) to).getWiidgetClass();

            href = builder.of(wiidgetClass);

        } else {
            href = builder.of((String) to);
        }

        getByPassAttributes().put("href", href);

        final Tag tag = createTag();

        if (Strings.isEmpty(buffer)) {
            tag.addChild(getLabel());
        } else {
            tag.addChild(buffer);
        }

        write(tag);
    }

    /**
     * @return the to
     */
    public Object getTo() {
        return to;
    }

    /**
     * @param to
     *            the to to set
     */
    public void setTo(final Object to) {
        this.to = to;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label
     *            the label to set
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    @Override
    public String getTagName() {
        return "a";
    }
}
