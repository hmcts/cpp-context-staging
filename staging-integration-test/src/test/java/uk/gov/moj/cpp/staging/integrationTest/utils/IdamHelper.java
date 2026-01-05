package uk.gov.moj.cpp.staging.integrationTest.utils;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static java.util.UUID.randomUUID;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.EMAIL_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.FIRST_NAME_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.IDAM_ACCOUNT_TYPE_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.IDAM_ID_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.IDAM_ORG_ID_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.LAST_NAME_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.MIDDLE_NAME_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.ORGANISATION_ADDRESS1_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.ORGANISATION_ADDRESS2_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.ORGANISATION_ADDRESS3_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.ORGANISATION_ADDRESS4_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.ORGANISATION_ADDRESS_POSTCODE_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.ORGANISATION_EMAIL_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.ORGANISATION_ID_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.ORGANISATION_NAME_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.ORGANISATION_PHONE_NUMBER_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.ORGANISATION_TYPE_PROPERTY;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.TITLE_PROPERTY;

import uk.gov.justice.service.wiremock.testutil.InternalEndpointMockUtils;
import uk.gov.justice.services.common.http.HeaderConstants;
import uk.gov.justice.services.test.utils.core.rest.RestClient;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.google.common.io.Resources;
import io.restassured.path.json.JsonPath;
import org.awaitility.Durations;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdamHelper {

    public static final String userCreateCommand = "/usersgroups-service/command/api/rest/usersgroups/users/3abb0c29-de0c-4b92-918f-1b87d05453bb";
    public static final String set_user_details_content_type = "application/vnd.usersgroups.user-details+json";
    protected static final int DEFAULT_MAX_TIMEOUT_FOR_ASSERTION = 60000;
    private static final Logger LOGGER = LoggerFactory.getLogger(IdamHelper.class);
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String IDAM_SYSTEM_USER_ID = randomUUID().toString();
    private static final String SESSION_ID = randomUUID().toString();
    private static final long RETRIEVE_TIMEOUT = 20000;
    private String wireMockUrl;

    public static void verifyPostSetUserDetails(final String organizationId, final String userType) {
        waitAtMost(Durations.TEN_SECONDS).until(() -> {
                    final Stream<JSONObject> setUserDetails = getSetUserDetails();

                    return setUserDetails.anyMatch(
                            payload -> {
                                try {
                                    return payload.getString("userType").equals(userType)
                                            && payload.getString("organisationId").equals(organizationId);
                                } catch (JSONException e) {
                                    return false;
                                }
                            }
                    );
                }
        );
    }

    private static Stream<JSONObject> getSetUserDetails() {
        return findAll(postRequestedFor(urlPathEqualTo(userCreateCommand))
                .withHeader(CONTENT_TYPE, equalTo(set_user_details_content_type)))
                .stream()
                .map(LoggedRequest::getBodyAsString)
                .map(JSONObject::new);
    }

    private static JsonPath retrieveMessage(final MessageConsumer consumer, long customTimeOutInMillis) {
        try {
            TextMessage message = (TextMessage) consumer.receive(customTimeOutInMillis);
            if (message == null) {
                LOGGER.error("No message retrieved using consumer with selector {}", consumer.getMessageSelector());
                return null;
            }
            return new JsonPath(message.getText());
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getCallCountToWiremock(Integer callCountBeforeTest) {

        int callCountAfterTest = WiremockTestHelper.getHitsCountOnMockedEndpoint(wireMockUrl);

        if (callCountAfterTest > callCountBeforeTest) {
            return callCountAfterTest;
        }
        return null;
    }

    public void stubPostUsersGroups(RestClient restClient, String server) {

        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.putSingle(HeaderConstants.USER_ID, IDAM_SYSTEM_USER_ID);
        InternalEndpointMockUtils.stubPingFor("usersgroups-service");

        String usersgroupsAddUserResourceUri = userCreateCommand;
        stubFor(post(urlMatching(usersgroupsAddUserResourceUri))
                .withHeader(CONTENT_TYPE_HEADER, equalTo(set_user_details_content_type))
                .withHeader(HeaderConstants.USER_ID, equalTo(IDAM_SYSTEM_USER_ID))
                .willReturn(aResponse().withStatus(202)));

        await().atMost(5, TimeUnit.SECONDS).until(()
                -> restClient.postCommand(server + usersgroupsAddUserResourceUri, "application/vnd.usersgroups.user-details+json", "{}", headers).getStatus() == 202);
    }

    public Integer waitAndGetCallCount(Integer callCountBeforeTest) {
        AsynchGetUntilResult<Integer, Integer> asynchGetUntilResultWiremockCallCount =
                new AsynchGetUntilResult<>(this::getCallCountToWiremock, callCountBeforeTest);

        await().atMost(DEFAULT_MAX_TIMEOUT_FOR_ASSERTION, TimeUnit.MILLISECONDS)
                .until(asynchGetUntilResultWiremockCallCount);

        return asynchGetUntilResultWiremockCallCount.getResult();
    }

    public String getUIdamUserIdFromRaml(String command) {
        JsonReader jsonReader = Json.createReader(new StringReader(getPayload(command)));
        JsonObject payload = jsonReader.readObject();
        jsonReader.close();

        return payload.getString("idamId");
    }

    public MultivaluedMap<String, Object> getHeaders() {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.putSingle(HeaderConstants.USER_ID, IDAM_SYSTEM_USER_ID);
        headers.putSingle(HeaderConstants.SESSION_ID, SESSION_ID);
        return headers;
    }

    public JsonPath verifyInActiveMQ(MessageConsumer consumer) {
        JsonPath jsonResponse = retrieveMessage(consumer, RETRIEVE_TIMEOUT);
        assertThat(jsonResponse, notNullValue());
        return jsonResponse;
    }

    @SuppressWarnings("unchecked")
    public void assertAccount(JsonPath jsonResponse, JSONObject payload) {

        assertThat(jsonResponse.getJsonObject("idamEvent"), notNullValue());
        Map responsePayload = ((Map<String, Map<String, String>>) jsonResponse.getJsonObject("idamEvent")).get("payload");
        assertThat(responsePayload.get(IDAM_ID_PROPERTY), is(payload.getString(IDAM_ID_PROPERTY)));
        assertThat(responsePayload.get(ORGANISATION_ID_PROPERTY), is(payload.getString(ORGANISATION_ID_PROPERTY)));
        assertThat(responsePayload.get(IDAM_ACCOUNT_TYPE_PROPERTY), is(payload.getString(IDAM_ACCOUNT_TYPE_PROPERTY)));
        assertThat(responsePayload.get(TITLE_PROPERTY), is(payload.getString(TITLE_PROPERTY)));
        assertThat(responsePayload.get(FIRST_NAME_PROPERTY), is(payload.getString(FIRST_NAME_PROPERTY)));
        assertThat(responsePayload.get(MIDDLE_NAME_PROPERTY), is(payload.getString(MIDDLE_NAME_PROPERTY)));
        assertThat(responsePayload.get(LAST_NAME_PROPERTY), is(payload.getString(LAST_NAME_PROPERTY)));
        assertThat(responsePayload.get(EMAIL_PROPERTY), is(payload.getString(EMAIL_PROPERTY)));

    }

    @SuppressWarnings("unchecked")
    public void assertOrganisation(JsonPath jsonResponse, JSONObject payload) {
        assertThat(jsonResponse.getJsonObject("idamEvent"), notNullValue());
        Map responsePayload = ((Map<String, Map<String, String>>) jsonResponse.getJsonObject("idamEvent")).get("payload");
        assertThat(responsePayload.get(IDAM_ORG_ID_PROPERTY), is(payload.getString(IDAM_ORG_ID_PROPERTY)));
        assertThat(responsePayload.get(ORGANISATION_TYPE_PROPERTY), is(payload.getString(ORGANISATION_TYPE_PROPERTY)));
        assertThat(responsePayload.get(ORGANISATION_NAME_PROPERTY), is(payload.getString(ORGANISATION_NAME_PROPERTY)));
        assertThat(responsePayload.get(ORGANISATION_ADDRESS1_PROPERTY), is(payload.getString(ORGANISATION_ADDRESS1_PROPERTY)));
        assertThat(responsePayload.get(ORGANISATION_ADDRESS2_PROPERTY), is(payload.getString(ORGANISATION_ADDRESS2_PROPERTY)));
        assertThat(responsePayload.get(ORGANISATION_ADDRESS3_PROPERTY), is(payload.getString(ORGANISATION_ADDRESS3_PROPERTY)));
        assertThat(responsePayload.get(ORGANISATION_ADDRESS4_PROPERTY), is(payload.getString(ORGANISATION_ADDRESS4_PROPERTY)));
        assertThat(responsePayload.get(ORGANISATION_ADDRESS_POSTCODE_PROPERTY), is(payload.getString(ORGANISATION_ADDRESS_POSTCODE_PROPERTY)));
        assertThat(responsePayload.get(ORGANISATION_PHONE_NUMBER_PROPERTY), is(payload.getString(ORGANISATION_PHONE_NUMBER_PROPERTY)));
        assertThat(responsePayload.get(ORGANISATION_EMAIL_PROPERTY), is(payload.getString(ORGANISATION_EMAIL_PROPERTY)));
    }

    private String getPayload(String path) {
        String request = null;

        try {
            request = Resources.toString(
                    Resources.getResource(path),
                    Charset.defaultCharset()
            );
        } catch (Exception e) {
            LOGGER.error("Error consuming file from location {}", path);
            fail("Error consuming file from location " + path);
        }
        return request;
    }


    public void setWireMockUrl(String wireMockUrl) {
        this.wireMockUrl = wireMockUrl;
    }

}
