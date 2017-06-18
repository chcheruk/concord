package com.walmartlabs.concord.server.cfg;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Properties;

@Named
@Singleton
public class SlackConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SlackConfiguration.class);

    private static final String SLACK_CFG_KEY = "SLACK_CFG";

    private final String authToken;
    private final String proxyAddress;
    private final Integer proxyPort;
    private final int connectTimeout;
    private final int soTimeout;
    private final int maxConnections;
    private final double requestLimit;

    public SlackConfiguration() throws IOException {
        Properties props = new Properties();

        String path = System.getenv(SLACK_CFG_KEY);
        if (path != null) {
            try (InputStream in = Files.newInputStream(Paths.get(path))) {
                props.load(in);
            }

            log.info("init -> using external Slack configuration: {}", path);

            this.authToken = props.getProperty("authToken");
            this.proxyAddress = Strings.emptyToNull(props.getProperty("proxyAddress"));
            this.proxyPort = getInteger(props, "proxyPort", null);
            this.connectTimeout = getInteger(props, "connectTimeout", 30_000);
            this.soTimeout = getInteger(props, "soTimeout", 30_000);
            this.maxConnections = getInteger(props, "maxConnections", 10);
            this.requestLimit = getDouble(props, "requestLimit", 1.0);
        } else {
            this.authToken = "invalid-token";
            this.proxyAddress = null;
            this.proxyPort = null;
            this.connectTimeout = 30_000;
            this.soTimeout = 30_000;
            this.maxConnections = 10;
            this.requestLimit = 1.0;

            log.info("init -> no Slack configuration");
        }
    }

    public static Number getNumber(Properties props, String name) {
        String value = props.getProperty(name);
        if(value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return NumberFormat.getInstance().parse(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid prop '" + name + "' format", e);
        }
    }

    private static double getDouble(Properties props, String name, double defaultValue) {
        Number value = getNumber(props, name);
        if(value != null) {
            return value.doubleValue();
        }

        return defaultValue;
    }

    private static Integer getInteger(Properties props, String name, Integer defaultValue) {
        Number value = getNumber(props, name);
        if(value != null) {
            return value.intValue();
        }

        return defaultValue;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getProxyAddress() {
        return proxyAddress;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public double getRequestLimit() {
        return requestLimit;
    }
}
