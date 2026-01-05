package uk.gov.moj.cpp.staging.integrationTest.utils;


import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.Charset;

import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    protected static final int DEFAULT_DELAY_INTERVAL_IN_MILLIS = 5000;

    /**
     * Waits for the time identified by field {@value DEFAULT_DELAY_INTERVAL_IN_MILLIS}
     *
     * @throws InterruptedException
     */
    public static void waitForTimeToElapse() {
        waitForTimeToElapse(DEFAULT_DELAY_INTERVAL_IN_MILLIS);
    }

    /**
     * Waits for the specified period of time
     *
     * @param millis
     * @throws InterruptedException
     */
    public static void waitForTimeToElapse(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.error("Error waiting for thread delay", e);
        }
    }

    public static String readFile(String ramlPath) {
        String request = null;
        try {
            request = Resources.toString(
                    Resources.getResource(ramlPath),
                    Charset.defaultCharset()
            );
        } catch (Exception e) {
            LOGGER.error("Error consuming file from location {}", ramlPath);
            fail("Error consuming file from location " + ramlPath);
        }
        return request;
    }

    public static String readFile(final String path, final Object... placeholders) {
        return String.format(readFile(path), placeholders);
    }

}
