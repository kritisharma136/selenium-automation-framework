package com.project.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader loads config.properties from the classpath.
 * Allows switching environments by passing -Denv=staging etc.
 */
public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static Properties properties = new Properties();

    static {
        String env = System.getProperty("env", "config"); // default: config.properties
        String fileName = env + ".properties";

        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config/" + fileName)) {

            if (input == null) {
                throw new RuntimeException("Cannot find " + fileName + " in classpath");
            }
            properties.load(input);
            log.info("Loaded config: {}", fileName);

        } catch (Exception e) {
            log.error("Failed to load config file", e);
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        // System property overrides file property — useful in Jenkins pipelines
        String value = System.getProperty(key, properties.getProperty(key));
        if (value == null) {
            throw new RuntimeException("Property not found: " + key);
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        return System.getProperty(key, properties.getProperty(key, defaultValue));
    }
}
