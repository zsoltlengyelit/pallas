package io.pallas.core.url;

import io.pallas.core.annotations.Component;
import io.pallas.core.configuration.Configurable;
import io.pallas.core.configuration.ConfigurationException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;

/**
 * @author Zsolt Lengyel (zsolt.lengyel.it@gmail.com)
 */
@Component("urlManager")
public class UrlManager implements Configurable {

    private final List<UrlRule> rules = new ArrayList<>();

    public ActionRequest parseRequest(final HttpServletRequest request) {

        for (final UrlRule urlRule : rules) {
            final ActionRequest parseRequest = urlRule.parseRequest(this, request);
        }

        return null;
    }

    @Override
    public void handleConfiguration(final Map<String, Object> configuration) {

        final Map<String, Object> cloneConfig = new HashMap<String, Object>(configuration);

        final Map<Object, Object> rulesObject = (Map<Object, Object>) cloneConfig.get("rules");
        for (final Entry<Object, Object> urlRule : rulesObject.entrySet()) {
            final String name = urlRule.getKey().toString();
            final String pattern = urlRule.getValue().toString();

            final UrlRule rule = new RegexUrlRule(name, pattern);
            rules.add(rule);
        }

        cloneConfig.remove("rules");

        for (final Entry<String, Object> property : configuration.entrySet()) {

            final String propertyName = property.getKey();

            try {
                BeanUtils.setProperty(this, propertyName, property.getValue());
            } catch (final IllegalAccessException exception) {
                throw new ConfigurationException(String.format("Cannot set '%s' property on bean", exception));
            } catch (final InvocationTargetException exception) {
                throw new ConfigurationException(String.format("Cannot set '%s' property on bean", exception));
            }
        }
    }
}
