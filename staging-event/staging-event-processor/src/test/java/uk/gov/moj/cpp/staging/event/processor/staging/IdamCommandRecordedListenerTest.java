package uk.gov.moj.cpp.staging.event.processor.staging;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static uk.gov.justice.services.core.annotation.Component.EVENT_PROCESSOR;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerClassMatcher.isHandlerClass;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerMethodMatcher.method;
import static uk.gov.justice.services.test.utils.core.messaging.MetadataBuilderFactory.metadataWithRandomUUID;

import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.staging.event.processor.staging.helper.IdamEventProcessorHelper;
import uk.gov.moj.cpp.staging.event.processor.staging.helper.OrganisationCreatedIdamEventHelper;
import uk.gov.moj.cpp.staging.event.processor.staging.helper.OrganisationUpdatedIdamEventHelper;

import java.util.UUID;

import uk.gov.justice.services.messaging.JsonObjects;
import jakarta.json.JsonValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IdamCommandRecordedListenerTest {

    private static final String COMMAND_NAME = "staging.record-idam-command";
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
    private static final String IDAM_ACCOUNT_TYPE = "idamAccountType";
    private static final String USER_TYPE = "userType";
    private static final String IDAM_ACCOUNT_TYPE_VALUE = "idamAccountTypeValue";
    @Mock
    JsonEnvelope jsonEnvelope;

    @InjectMocks
    private IdamCommandRecordedListener idamCommandRecordedListener;

    @Mock
    private IdamEventProcessorHelper idamEventProcessorHelper;

    @Mock
    private OrganisationCreatedIdamEventHelper organisationCreatedIdamEventHelper;
    @Mock
    private Sender sender;
    @Mock
    private OrganisationUpdatedIdamEventHelper organisationUpdatedIdamEventHelper;

    @Captor
    private ArgumentCaptor<JsonEnvelope> jsonObjectCaptor;

    @Test
    void testClassWiring() {
        assertThat(IdamCommandRecordedListener.class, isHandlerClass(EVENT_PROCESSOR).with(
                method("handle").thatHandles(COMMAND_NAME)
        ));
    }

    @Test
    void shouldCallHandle() {
        idamCommandRecordedListener.initialize();
        final JsonValue updateUserEvent = JsonObjects.createObjectBuilder()
                .add(IDAM_ID, IDAM_ID_VALUE)
                .add(IDAM_ORG_ID, IDAM_ORG_ID_VALUE)
                .add(FIRST_NAME, FIRST_NAME_VALUE)
                .add(LAST_NAME, LAST_NAME_VALUE)
                .add(IDAM_ACCOUNT_TYPE, IDAM_ACCOUNT_TYPE_VALUE)
                .add(EMAIL, EMAIL_VALUE)
                .build();

        final JsonValue metaData = JsonObjects.createObjectBuilder()
                .add("name",COMMAND_NAME)
                .add("id", UUID.randomUUID().toString())
                .build();

        assertThat(metaData, is(notNullValue()));

        jsonEnvelope = buildCppMessageForIdamCommandRecordedEvent(updateUserEvent,metaData);
        assertThat(jsonEnvelope.asJsonObject().get("idamEvent"), is(notNullValue()));

        idamCommandRecordedListener.handle(jsonEnvelope);
    }

    private JsonEnvelope buildCppMessageForIdamCommandRecordedEvent(JsonValue idamPayload, final JsonValue metaData) {
        final JsonValue idamEvent = JsonObjects.createObjectBuilder()
                .add("_metadata", metaData)
                .add("payload", idamPayload).build();

        final JsonValue cppPayload = JsonObjects.createObjectBuilder().add("idamEvent", idamEvent).build();
        assertThat(cppPayload, is(notNullValue()));
        return envelopeFrom(metadataWithRandomUUID(COMMAND_NAME), cppPayload);
    }


}