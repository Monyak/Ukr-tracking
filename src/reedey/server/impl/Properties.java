package reedey.server.impl;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Properties {
    private static final String BUNDLE_NAME = "reedey.server.impl.server"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    private Properties() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
