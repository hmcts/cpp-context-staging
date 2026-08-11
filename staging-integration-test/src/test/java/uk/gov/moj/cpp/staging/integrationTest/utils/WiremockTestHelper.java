package uk.gov.moj.cpp.staging.integrationTest.utils;


import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.gov.justice.services.test.utils.core.http.RequestParamsBuilder.requestParams;
import static uk.gov.justice.services.test.utils.core.http.RestPoller.poll;
import static uk.gov.justice.services.test.utils.core.matchers.ResponsePayloadMatcher.payload;
import static uk.gov.justice.services.test.utils.core.matchers.ResponseStatusMatcher.status;

import uk.gov.justice.services.test.utils.core.http.RequestParams;
import uk.gov.justice.services.test.utils.core.rest.RestClient;

import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Provides helper methods for tests to interact with Wiremock instance
 */
public class WiremockTestHelper {

    public static final String HOST = System.getProperty("INTEGRATION_HOST_KEY", "localhost");
    public static final String BASE_URI = "http://" + HOST + ":8080";
    private static final Logger LOGGER = LoggerFactory.getLogger(WiremockTestHelper.class);
    private static final String WIREMOCK_PORT = System.getProperty("WIREMOCK_PORT", "8080");
    private static final String WIREMOCK_BASE_URI = "http://" + HOST + ":" + WIREMOCK_PORT;
    private static final String WIREMOCK_COUNT_URI = WIREMOCK_BASE_URI + "/__admin/requests/count";

    private static final RestClient restClient = new RestClient();

    public static void resetService() {
        configureFor(HOST, 8080);
        reset();
    }

    public static int getHitsCountOnMockedEndpoint(final String url) {
        final Response response = restClient.postCommand(WIREMOCK_COUNT_URI, MediaType.APPLICATION_JSON, buildGetHitsCountPayload(url));
        if (response.getStatus() != Status.OK.getStatusCode()) {
            LOGGER.error("Failed to get count of hits to mocked endpoint: {}", url);
            fail("Failed to get count of hits to mocked endpoint. Received " + response.getStatus() + " status code.");
        }
        final String responsePayloadStr = response.readEntity(String.class);
        final JSONObject responsePayload = new JSONObject(responsePayloadStr);
        return responsePayload.getInt("count");
    }

    public static void waitForStubToBeReady(final String resource, final String mediaType) {
        waitForStubToBeReady(resource, mediaType, Status.OK);
    }

    public static void waitForStubToBeReady(final String resource, final String mediaType, final Status expectedStatus) {
        final RequestParams requestParams = requestParams(BASE_URI + resource, mediaType).build();

        poll(requestParams)
                .until(
                        status().is(expectedStatus)
                );
    }

    public static void waitForStubToBeReady(final String resource, final String mediaType, final String expectedInBody) {
        final RequestParams requestParams = requestParams(BASE_URI + resource, mediaType).build();

        poll(requestParams)
                .until(
                        status().is(Status.OK),
                        payload().that(containsString(expectedInBody))
                );
    }

    public static void waitForStubToBeReady(final String resource, final String mediaType, final Status expectedStatus, final String headerName, final String headerValue) {
        final RequestParams requestParams = requestParams(BASE_URI + resource, mediaType)
                .withHeader(headerName, headerValue)
                .build();
        poll(requestParams)
                .until(
                        status().is(expectedStatus)
                );
    }

    public static void validateNumberOfStructureInteractionsIs(final int expectedCount, final String url) {
        final RequestPatternBuilder requestPatternBuilder = postRequestedFor(urlPathMatching(url));

        await().atMost(40, TimeUnit.SECONDS)
                .until(() -> findAll(requestPatternBuilder).size() > 0);

        verify(expectedCount, requestPatternBuilder);
    }

    private static String buildGetHitsCountPayload(final String url) {
        final JSONObject jsonData = new JSONObject();
        jsonData.put("method", "POST");
        jsonData.put("urlPattern", url);
        return jsonData.toString();
    }

}
