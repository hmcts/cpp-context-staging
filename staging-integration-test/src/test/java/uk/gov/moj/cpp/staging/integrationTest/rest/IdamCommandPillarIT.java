package uk.gov.moj.cpp.staging.integrationTest.rest;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import uk.gov.justice.services.common.http.HeaderConstants;

import java.util.UUID;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class IdamCommandPillarIT extends BaseIT {

    private final String RESOURCE = "/identity.event";
    private final String CONTENT_TYPE = "application/vnd.staging.record-idam-command+json";
    private final static String COMMAND = "raml/json/staging.record-idam-command.json";
    private final static String SCHEMA = "raml/json/schema/staging.record-idam-command.json";

    private MultivaluedMap<String, Object> headersMap = new MultivaluedHashMap<>();

    @Test
    public void sendRequestAndWaitForEvent() throws Exception {
        headersMap.add(HeaderConstants.USER_ID, UUID.randomUUID().toString());
        JSONObject payload = new JSONObject(getPayload(COMMAND));

        String url = String.format("%s%s%s", BASE_URI, WRITE_BASE_URL, RESOURCE);
        Response response = restClient.postCommand(url, CONTENT_TYPE, payload.toString(), headersMap);
        assertThat(response.getStatus(), is(HttpStatus.SC_ACCEPTED));

        JSONObject eventPayload = readActiveMq();

        assertThat(eventPayload, is(notNullValue()));
        assertThat(eventPayload.getJSONObject("idamEvent"), notNullValue());
        validateSchema(SCHEMA, eventPayload);
    }

    public void createConsumer() throws Exception {
        String selector = "CPPNAME IN ('staging.record-idam-command')";
        consumer = session.createConsumer(topic, selector);
    }

}

