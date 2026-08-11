package uk.gov.moj.cpp.staging.integrationTest.rest.idam;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.moj.cpp.staging.integrationTest.rest.idam.IdamConstants.RESOURCE;

import uk.gov.moj.cpp.staging.integrationTest.rest.BaseIT;
import uk.gov.moj.cpp.staging.integrationTest.utils.IdamHelper;

import jakarta.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class NonCppIdamEventsIT extends BaseIT {

    private IdamHelper idamHelper;

    @BeforeEach
    public void setup() {
        idamHelper = new IdamHelper();
    }

    @Override
    protected void createConsumer() throws Exception {
        consumer = session.createConsumer(topic, "CPPNAME IN ('staging.record-idam-command')", true);
    }

    @Test
    public void shouldAcceptAccountCreatedEvents() throws Exception {
        sendRequestAndWaitForEvent("identity.events.account-created", "application/vnd.identity.events.account-created+json");
    }

    @Test
    public void shouldAcceptAccountSuspensionSetupEvents() throws Exception {
        sendRequestAndWaitForEvent("identity.events.account-suspension-setup", "application/vnd.identity.events.account-suspension-setup+json");
    }

    @Test
    public void shouldAcceptAccountSuspensionUpdatedEvents() throws Exception {
        sendRequestAndWaitForEvent("identity.events.account-suspension-updated", "application/vnd.identity.events.account-suspension-updated+json");
    }

    @Test
    public void shouldAcceptAccountDeregisteredEvents() throws Exception {
        sendRequestAndWaitForEvent("identity.events.account-deregistered", "application/vnd.identity.events.account-deregistered+json");
    }

    @Test
    public void shouldAcceptAccountReregisteredEvents() throws Exception {
        sendRequestAndWaitForEvent("identity.events.account-reregistered", "application/vnd.identity.events.account-reregistered+json");
    }

    @Test
    public void shouldAcceptAccountDeletedEvents() throws Exception {
        sendRequestAndWaitForEvent("identity.events.account-deleted", "application/vnd.identity.events.account-deleted+json");
    }

    @Test
    public void shouldAcceptOrganisationCreatedEvents() throws Exception {
        sendRequestAndWaitForEvent("identity.events.organisation-created", "application/vnd.identity.events.organisation-created+json");
    }

    @Test
    public void shouldAcceptOrganisationUpdatedEvents() throws Exception {
        sendRequestAndWaitForEvent("identity.events.organisation-updated", "application/vnd.identity.events.organisation-updated+json");
    }

    @Test
    public void shouldAcceptOrganisationDeregisteredEvents() throws Exception {
        sendRequestAndWaitForEvent("identity.events.organisation-deregistered", "application/vnd.identity.events.organisation-deregistered+json");
    }

    @Test
    public void shouldAcceptOrganisationReregisteredEvents() throws Exception {
        sendRequestAndWaitForEvent("identity.events.organisation-reregistered", "application/vnd.identity.events.organisation-reregistered+json");
    }

    @Test
    public void shouldAcceptOrganisationDeletedEvents() throws Exception {
        sendRequestAndWaitForEvent("identity.events.organisation-deleted", "application/vnd.identity.events.organisation-deleted+json");
    }

    @Test
    public void shouldAcceptOrganisationSynchronisationEvents() throws Exception {
        sendRequestAndWaitForEvent("identity.events.organisation-synchronisation", "application/vnd.identity.events.organisation-synchronisation+json");
    }

    private void sendRequestAndWaitForEvent(final String eventName, final String contentType) throws Exception {

        JSONObject payload = new JSONObject(getPayload("json/identity/identity.events.noncpp.json").replaceAll("IDAM-EVENT-NAME", eventName));

        Response response = restClient.postCommand(BASE_URI + EVENT_BASE_URL + RESOURCE, contentType, payload.toString(), idamHelper.getHeaders());
        assertThat(response.getStatus(), is(HttpStatus.SC_ACCEPTED));

        JSONObject eventPayload = readActiveMq();

        JSONObject actualPayload = eventPayload.getJSONObject("idamEvent").getJSONObject("payload");

        payload.remove("_metadata");

        JSONAssert.assertEquals(payload, actualPayload, false);
    }
}
