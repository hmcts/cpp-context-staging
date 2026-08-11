package uk.gov.moj.cpp.staging.integrationTest.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.gov.justice.services.messaging.JsonEnvelope.METADATA;
import static uk.gov.moj.cpp.staging.integrationTest.utils.WiremockTestHelper.resetService;
import static uk.gov.moj.cpp.staging.integrationTest.utils.WiremockTestHelper.waitForStubToBeReady;

import uk.gov.justice.service.wiremock.testutil.InternalEndpointMockUtils;
import uk.gov.justice.services.test.utils.core.messaging.MessageConsumerClient;
import uk.gov.justice.services.test.utils.core.rest.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;
import jakarta.json.JsonObject;

import com.google.common.io.Resources;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a base class to provide common functionality for all integration tests across the
 * Staging context.
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseIT {

    private final Logger LOGGER = LoggerFactory.getLogger(BaseIT.class);

    public static final String HOST = System.getProperty("INTEGRATION_HOST_KEY", "localhost");
    public static final String BASE_URI = System.getProperty("baseUri", "http://" + HOST + ":8080");
    protected static final String MOCK_SERVER = "http://" + HOST + ":8080";

    public static final String WRITE_BASE_URL = "/staging-command-api/command/api/rest/staging";
    protected static final String EVENT_BASE_URL = "/staging-event-api/event/api/rest/staging";

    private final long RECEIVE_TIMEOUT = 30_000;

    public static final String ACTIVE_MQ_BROKER_URL = "tcp://" + HOST + ":61616";
    public static final String STAGING_ACTIVE_MQ_TOPIC = "jms.topic.staging.event";

    protected RestClient restClient = new RestClient();

    private Connection connection;
    protected Session session;
    protected ActiveMQTopic topic;
    protected MessageConsumer consumer;
    protected MessageConsumerClient publicConsumer;

    @BeforeEach
    public void beforeAllTests() throws Exception {
        resetService();
        stubUserAndGroups();
        publicConsumer = new MessageConsumerClient();
        setupActiveMq();
    }

    /**
     * Set up the ActiveMQ communication so that it is possible to read {@link JSONObject} from the
     * topic using {@link BaseIT#readActiveMq()}
     */
    public void setupActiveMq() throws Exception {
        final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVE_MQ_BROKER_URL);
        connection = factory.createConnection();
        connection.start();

        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        topic = new ActiveMQTopic(STAGING_ACTIVE_MQ_TOPIC);
        createConsumer();
    }

    @AfterEach
    public void afterTheTest() throws Exception {
        if (consumer != null) {
            consumer.close();
        }
        publicConsumer.close();
        connection.close();
    }

    private void stubUserAndGroups() throws IOException {
        InternalEndpointMockUtils.stubPingFor("usersgroups-service");
        String usersgroupsGetUserByUserIdUri = "/usersgroups-service/query/api/rest/usersgroups/users/.*";
        final String userDetails = getJsonResponse("mockFiles/usersgroups.query.user.json");
        JSONObject jsonObject = new JSONObject(userDetails);
        stubFor(get(urlPathMatching(usersgroupsGetUserByUserIdUri)).willReturn(aResponse().withStatus(200)
                .withBody(jsonObject.toString())));

        waitForStubToBeReady("/usersgroups-service/query/api/rest/usersgroups/users/" + randomUUID().toString(), "application/vnd.usersgroups.query.user+json");
    }

    private String getJsonResponse(String jsonFile) throws IOException {
        return Resources.toString(Resources.getResource(jsonFile),
                Charset.defaultCharset());
    }

    protected abstract void createConsumer() throws Exception;

    /**
     * Gets the {@link String} payload from the given file
     *
     * @param path {@link String} defining the location of the payload file
     * @return {@link String} the payload
     */
    protected String getPayload(String path) {
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

    /**
     * Validate that the event data matches the supplied schema
     *
     * @param schemaUri {@link String} defining the location to the schema file
     * @param event     {@link JsonObject} containing the event to compare
     */
    protected void validateSchema(String schemaUri, JSONObject event) throws Exception {
        try (InputStream inputStream = Resources.getResource(schemaUri).openStream()) {

            JSONObject schemaObject = new JSONObject(new JSONTokener(inputStream));

            Schema schema = SchemaLoader.load(schemaObject);
            schema.validate(event);
        } catch (Exception e) {
            LOGGER.error("Caught exception trying to parse schema: %s", e);
            throw e;
        }
    }

    /**
     * Read a {@link JSONObject} event from the ActiveMQ topic
     *
     * @return {@link JSONObject} containing the event read from the ActiveMQ topic
     */
    protected JSONObject readActiveMq() throws JMSException {
        JSONObject jsonObject = readFromMq();
        jsonObject.remove(METADATA);
        return jsonObject;
    }

    private JSONObject readFromMq() throws JMSException {
        Message message = consumer.receive(RECEIVE_TIMEOUT);
        assertNotNull(message, "Timeout waiting for message");
        ActiveMQTextMessage amqTextMessage = (ActiveMQTextMessage) message;
        JSONObject jsonObject = new JSONObject(amqTextMessage.getText());
        return jsonObject;
    }

}
