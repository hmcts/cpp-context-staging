package uk.gov.moj.cpp.staging.event.processor.staging.helper;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopeMatcher.jsonEnvelope;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopeMetadataMatcher.withMetadataEnvelopedFrom;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopePayloadMatcher.payloadIsJson;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithRandomUUID;

import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.test.utils.core.enveloper.EnveloperFactory;

import java.util.UUID;

import uk.gov.justice.services.messaging.JsonObjects;
import jakarta.json.JsonArray;
import jakarta.json.JsonValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IdamEventProcessorHelperTest {

    private static final String IDAM_ID = "idamId";
    private static final String IDAM_ORG_ID = "idamOrgId";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL = "email";
    private static final String IDAM_ID_VALUE = UUID.randomUUID().toString();
    private static final String IDAM_ORG_ID_VALUE = UUID.randomUUID().toString();
    private static final String EMAIL_VALUE = "test@test.com";
    private static final String FIRST_NAME_VALUE = "Josephine";
    private static final String LAST_NAME_VALUE = "Bloggs";
    private static final String USER_ID = "userId";
    private static final String ORGANISATION_ID = "organisationId";
    private static final String SOURCE = "source";
    private static final String SOURCE_VALUE = "IDAM";
    private static final String IDAM_SERVICES_ENROLMENT = "idamServicesEnrolment";
    private static final String IDAM_ACCOUNT_TYPE = "idamAccountType";
    private static final String USER_TYPE = "userType";
    private static final String IDAM_ACCOUNT_TYPE_VALUE = "idamAccountTypeValue";

    @Mock
    private Sender sender;

    @Spy
    private Enveloper enveloper = EnveloperFactory.createEnveloper();

    @InjectMocks
    private IdamEventProcessorHelper idamEventProcessorHelper;

    @Captor
    private ArgumentCaptor<JsonEnvelope> jsonObjectCaptor;

    private JsonEnvelope jsonEnvelope;

    @Test
    public void shouldCreateNewUserCommandFromEnrolmentChangedEventForCppService() {
        final JsonArray servicesArray = JsonObjects.createArrayBuilder().add("CPP").build();
        final JsonValue enrolmentChangedEvent = JsonObjects.createObjectBuilder()
                .add(IDAM_ID, IDAM_ID_VALUE)
                .add(IDAM_ORG_ID, IDAM_ORG_ID_VALUE)
                .add(FIRST_NAME, FIRST_NAME_VALUE)
                .add(LAST_NAME, LAST_NAME_VALUE)
                .add(EMAIL, EMAIL_VALUE)
                .add(IDAM_ACCOUNT_TYPE, IDAM_ACCOUNT_TYPE_VALUE)
                .add(IDAM_SERVICES_ENROLMENT, servicesArray)
                .build();

        jsonEnvelope = buildCppMessageForIdamEvent(enrolmentChangedEvent);

        idamEventProcessorHelper.handleEnrolmentChangedEvent(jsonEnvelope);
        assertCreateUserCommandSent(jsonEnvelope);
    }

    @Test
    public void shouldNotCreateNewUserCommandFromEnrolmentChangedEventForNonCppService() {
        final JsonArray servicesArray = JsonObjects.createArrayBuilder().add("ROTA").build();
        final JsonValue enrolmentChangedEvent = JsonObjects.createObjectBuilder()
                .add(IDAM_ID, IDAM_ID_VALUE)
                .add(FIRST_NAME, FIRST_NAME_VALUE)
                .add(LAST_NAME, LAST_NAME_VALUE)
                .add(EMAIL, EMAIL_VALUE)
                .add(IDAM_SERVICES_ENROLMENT, servicesArray)
                .build();

        jsonEnvelope = buildCppMessageForIdamEvent(enrolmentChangedEvent);

        idamEventProcessorHelper.handleEnrolmentChangedEvent(jsonEnvelope);

        verify(sender, never()).send(any());
    }

    @Test
    public void shouldCreateNewUserCommandFromEnrolmentChangedEventForCppAndNonCppService() {
        final JsonArray servicesArray = JsonObjects.createArrayBuilder().add("CPP").add("ROTA").build();
        final JsonValue enrolmentChangedEvent = JsonObjects.createObjectBuilder()
                .add(IDAM_ID, IDAM_ID_VALUE)
                .add(IDAM_ORG_ID, IDAM_ORG_ID_VALUE)
                .add(FIRST_NAME, FIRST_NAME_VALUE)
                .add(LAST_NAME, LAST_NAME_VALUE)
                .add(EMAIL, EMAIL_VALUE)
                .add(IDAM_ACCOUNT_TYPE, IDAM_ACCOUNT_TYPE_VALUE)
                .add(IDAM_SERVICES_ENROLMENT, servicesArray)
                .build();

        jsonEnvelope = buildCppMessageForIdamEvent(enrolmentChangedEvent);

        idamEventProcessorHelper.handleEnrolmentChangedEvent(jsonEnvelope);
        assertCreateUserCommandSent(jsonEnvelope);
    }

    @Test
    public void shouldCreateUpdateUserCommandFromUserUpdatedEvent() {
        final JsonValue updateUserEvent = JsonObjects.createObjectBuilder()
                .add(IDAM_ID, IDAM_ID_VALUE)
                .add(IDAM_ORG_ID, IDAM_ORG_ID_VALUE)
                .add(FIRST_NAME, FIRST_NAME_VALUE)
                .add(LAST_NAME, LAST_NAME_VALUE)
                .add(IDAM_ACCOUNT_TYPE, IDAM_ACCOUNT_TYPE_VALUE)
                .add(EMAIL, EMAIL_VALUE)
                .build();

        jsonEnvelope = buildCppMessageForIdamEvent(updateUserEvent);

        idamEventProcessorHelper.handleAccountUpdatedEvent(jsonEnvelope);
        assertCreateUserCommandSent(jsonEnvelope);
    }

    @Test
    public void shouldCreateReRegisterCommandFromUserUpdatedEvent() {
        final JsonValue reRegisterUser = JsonObjects.createObjectBuilder()
                .add(IDAM_ID, IDAM_ID_VALUE)
                .add(IDAM_ORG_ID, IDAM_ORG_ID_VALUE)
                .build();

        jsonEnvelope = buildCppMessageForIdamEvent(reRegisterUser);

        idamEventProcessorHelper.handleUserReregisteredEvent(jsonEnvelope);
        assertReRegisterCommandSent(jsonEnvelope);
    }

    @Test
    public void shouldCreateDeRegisterCommandFromUserUpdatedEvent() {
        final JsonValue reRegisterUser = JsonObjects.createObjectBuilder()
                .add(IDAM_ID, IDAM_ID_VALUE)
                .add(IDAM_ORG_ID, IDAM_ORG_ID_VALUE)
                .build();

        jsonEnvelope = buildCppMessageForIdamEvent(reRegisterUser);

        idamEventProcessorHelper.handleUserDeregisteredEvent(jsonEnvelope);
        assertReRegisterCommandSent(jsonEnvelope);
    }

    private JsonEnvelope buildCppMessageForIdamEvent(JsonValue idamPayload) {
        final JsonValue idamEvent = JsonObjects.createObjectBuilder().add("payload", idamPayload).build();

        final JsonValue cppPayload = JsonObjects.createObjectBuilder().add("idamEvent", idamEvent).build();

        return envelopeFrom(metadataWithRandomUUID("staging.record-idam-command"), cppPayload);
    }

    private void assertReRegisterCommandSent(final JsonEnvelope sourceEvent) {
        verify(sender).send(jsonObjectCaptor.capture());
        final JsonEnvelope createUserCommand = jsonObjectCaptor.getValue();

        assertThat(createUserCommand, jsonEnvelope(withMetadataEnvelopedFrom(sourceEvent), payloadIsJson(
                allOf(
                        withJsonPath(USER_ID, equalTo(IDAM_ID_VALUE)),
                        withJsonPath(ORGANISATION_ID, equalTo(IDAM_ORG_ID_VALUE))
                ))));
    }
    private void assertCreateUserCommandSent(final JsonEnvelope sourceEvent) {
        verify(sender).send(jsonObjectCaptor.capture());
        final JsonEnvelope createUserCommand = jsonObjectCaptor.getValue();

        assertThat(createUserCommand, jsonEnvelope(withMetadataEnvelopedFrom(sourceEvent), payloadIsJson(
                allOf(
                        withJsonPath(USER_ID, equalTo(IDAM_ID_VALUE)),
                        withJsonPath(ORGANISATION_ID, equalTo(IDAM_ORG_ID_VALUE)),
                        withJsonPath(FIRST_NAME, equalTo(FIRST_NAME_VALUE)),
                        withJsonPath(LAST_NAME, equalTo(LAST_NAME_VALUE)),
                        withJsonPath(EMAIL, equalTo(EMAIL_VALUE)),
                        withJsonPath(SOURCE, equalTo(SOURCE_VALUE)),
                        withJsonPath(USER_TYPE, equalTo(IDAM_ACCOUNT_TYPE_VALUE))
                ))));
    }

}