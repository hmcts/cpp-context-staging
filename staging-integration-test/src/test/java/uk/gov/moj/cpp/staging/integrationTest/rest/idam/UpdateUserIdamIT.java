package uk.gov.moj.cpp.staging.integrationTest.rest.idam;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.RESOURCE;

import uk.gov.moj.cpp.staging.integrationTest.rest.BaseIT;
import uk.gov.moj.cpp.staging.integrationTest.utils.IdamHelper;
import uk.gov.moj.cpp.staging.integrationTest.utils.WiremockTestHelper;

import javax.ws.rs.core.Response;

import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateUserIdamIT extends BaseIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserIdamIT.class);
    private static final String ACCOUNT_UPDATED_CONTENT_TYPE = "application/vnd.identity.events.account-updated+json";
    private String WIREMOCK_VERIFY_URL = "/usersgroups-service/command/api/rest/usersgroups/users/";
    private static final String COMMAND = "json/identity/identity.events.account-updated.json";

    private IdamHelper idamHelper;

    @BeforeEach
    public void setup() {
        idamHelper = new IdamHelper();
        WIREMOCK_VERIFY_URL = WIREMOCK_VERIFY_URL + idamHelper.getUIdamUserIdFromRaml(COMMAND);
        idamHelper.setWireMockUrl(WIREMOCK_VERIFY_URL);
        idamHelper.stubPostUsersGroups(restClient, MOCK_SERVER);
    }

    @Override
    protected void createConsumer() throws Exception {
        consumer = session.createConsumer(topic, "CPPNAME IN ('staging.record-idam-command')");
    }

    @Test
    public void sendRequestAndWaitForEvent() throws Exception {
        int wiremockCallCountBeforeTest = WiremockTestHelper.getHitsCountOnMockedEndpoint(WIREMOCK_VERIFY_URL);

        LOGGER.info("CALL COUNT BEFORE TEST: {}", wiremockCallCountBeforeTest);

        JSONObject payload = new JSONObject(getPayload(COMMAND));

        Response response = restClient.postCommand(BASE_URI + EVENT_BASE_URL + RESOURCE, ACCOUNT_UPDATED_CONTENT_TYPE, payload.toString(), idamHelper.getHeaders());
        assertThat(response.getStatus(), is(HttpStatus.SC_ACCEPTED));
        JsonPath jsonResponse = idamHelper.verifyInActiveMQ(consumer);
        assertThat(idamHelper.waitAndGetCallCount(wiremockCallCountBeforeTest) - wiremockCallCountBeforeTest, is(1));

        idamHelper.assertAccount(jsonResponse, payload);
    }

}