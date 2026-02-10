package uk.gov.moj.cpp.staging.event.processor.staging.helper;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
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
import javax.json.JsonValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganisationUpdatedIdamEventHelperTest {
    private static final String IDAM_ORG_ID = "idamOrganisationId";
    private static final String IDAM_ORG_ID_VALUE = UUID.randomUUID().toString();;
    private static final String ORGANISATION_TYPE = "organisationType";
    private static final String  ORGANISATION_TYPE_VALUE = "LEGAL_ORGANISATION";
    private static final String ORGANISATION_NAME = "organisationName";
    private static final String  ORGANISATION_NAME_VALUE = "Bodgit and Scarper LLP";
    private static final String ORGANISATION_ADDRESS1 = "addressLine1";
    private static final String ORGANISATION_ADDRESS1_VALUE = "Legal House";
    private static final String ORGANISATION_ADDRESS2 = "addressLine2";
    private static final String ORGANISATION_ADDRESS2_VALUE = "15 Sewell Street";
    private static final String ORGANISATION_ADDRESS3 = "addressLine3";
    private static final String ORGANISATION_ADDRESS3_VALUE = "Hammersmith";
    private static final String ORGANISATION_ADDRESS4 = "addressLine4";
    private static final String ORGANISATION_ADDRESS4_VALUE = "London";
    private static final String ORGANISATION_ADDRESS_POSTCODE = "addressPostcode";
    private static final String ORGANISATION_ADDRESS_POSTCODE_VALUE = "SE14 2AB";
    private static final String ORGANISATION_PHONE_NUMBER = "phoneNumber";
    private static final String ORGANISATION_PHONE_NUMBER_VALUE = "080012345678";
    private static final String ORGANISATION_EMAIL = "email";
    private static final String ORGANISATION_EMAIL_VALUE = "joe@example.com";
    @Mock
    private Sender sender;

    @Spy
    private Enveloper enveloper = EnveloperFactory.createEnveloper();

    @InjectMocks
    private OrganisationUpdatedIdamEventHelper organisationUpdatedIdamEventHelper;

    @Captor
    private ArgumentCaptor<JsonEnvelope> jsonObjectCaptor;

    private JsonEnvelope jsonEnvelope;

    @Test
    void shouldProcessOrganisationUpdated() {
        final JsonValue organisationCreatedEvent = JsonObjects.createObjectBuilder()
                .add(IDAM_ORG_ID,IDAM_ORG_ID_VALUE)
                .add(ORGANISATION_TYPE,ORGANISATION_TYPE_VALUE)
                .add(ORGANISATION_NAME,ORGANISATION_NAME_VALUE)
                .add(ORGANISATION_ADDRESS1,ORGANISATION_ADDRESS1_VALUE)
                .add(ORGANISATION_ADDRESS2,ORGANISATION_ADDRESS2_VALUE)
                .add(ORGANISATION_ADDRESS3,ORGANISATION_ADDRESS3_VALUE)
                .add(ORGANISATION_ADDRESS4,ORGANISATION_ADDRESS4_VALUE)
                .add(ORGANISATION_ADDRESS_POSTCODE,ORGANISATION_ADDRESS_POSTCODE_VALUE)
                .add(ORGANISATION_PHONE_NUMBER,ORGANISATION_PHONE_NUMBER_VALUE)
                .add(ORGANISATION_EMAIL,ORGANISATION_EMAIL_VALUE)
                .build();

        jsonEnvelope = buildMessageForOrganisationUpdatedIdamEvent(organisationCreatedEvent);
        organisationUpdatedIdamEventHelper.processOrganisationUpdated(jsonEnvelope);
        assertOrganisationUpdatedCommandSent(jsonEnvelope);
    }
    private JsonEnvelope buildMessageForOrganisationUpdatedIdamEvent(JsonValue orgIdamPayload) {
        final JsonValue idamEvent = JsonObjects.createObjectBuilder().add("payload", orgIdamPayload).build();
        final JsonValue cppPayload = JsonObjects.createObjectBuilder().add("idamEvent", idamEvent).build();
        return envelopeFrom(metadataWithRandomUUID("identity.events.organisation-created"), cppPayload);
    }

    private void assertOrganisationUpdatedCommandSent(final JsonEnvelope sourceEvent) {
        verify(sender).send(jsonObjectCaptor.capture());
        final JsonEnvelope createOrgCommand = jsonObjectCaptor.getValue();
        assertThat(createOrgCommand, jsonEnvelope(withMetadataEnvelopedFrom(sourceEvent), payloadIsJson(
                allOf(
                        withJsonPath(ORGANISATION_TYPE,equalTo(ORGANISATION_TYPE_VALUE)),
                        withJsonPath(ORGANISATION_NAME,equalTo(ORGANISATION_NAME_VALUE)),
                        withJsonPath(ORGANISATION_ADDRESS1,equalTo(ORGANISATION_ADDRESS1_VALUE)),
                        withJsonPath(ORGANISATION_ADDRESS2,equalTo(ORGANISATION_ADDRESS2_VALUE)),
                        withJsonPath(ORGANISATION_ADDRESS3,equalTo(ORGANISATION_ADDRESS3_VALUE)),
                        withJsonPath(ORGANISATION_ADDRESS4,equalTo(ORGANISATION_ADDRESS4_VALUE)),
                        withJsonPath(ORGANISATION_ADDRESS_POSTCODE,equalTo(ORGANISATION_ADDRESS_POSTCODE_VALUE)),
                        withJsonPath(ORGANISATION_PHONE_NUMBER,equalTo(ORGANISATION_PHONE_NUMBER_VALUE)),
                        withJsonPath(ORGANISATION_EMAIL,equalTo(ORGANISATION_EMAIL_VALUE))

                ))));
    }
}