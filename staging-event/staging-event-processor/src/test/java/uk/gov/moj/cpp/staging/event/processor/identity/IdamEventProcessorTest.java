package uk.gov.moj.cpp.staging.event.processor.identity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.messaging.JsonEnvelope.envelopeFrom;

import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IdamEventProcessorTest {

    private static final String COMMAND_NAME = "staging.record-idam-command";

    @Mock
    JsonEnvelope envelope;

    @Mock
    JsonObject payload;

    @Mock
    private Sender sender;

    @Mock
    Enveloper enveloper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Metadata metadata;

    @Mock
    JsonValue jsonValue;

    @Mock
    JsonObject jsonObject;

    @InjectMocks
    private IdamEventProcessor idamEventProcessor;

    @BeforeEach
    public void setup() {
        when(envelope.payloadAsJsonObject()).thenReturn(payload);
        when(envelope.metadata()).thenReturn(metadata);

        when(enveloper.withMetadataFrom(eq(envelope), eq(COMMAND_NAME)))
                .thenReturn(x -> envelopeFrom(metadata, jsonValue));
    }

    @Test
    public void shouldHandleCreateUserAction() throws Exception {
        idamEventProcessor.createUserAccount(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleUpdateUserAction() throws Exception {
        idamEventProcessor.updateUserAccount(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleAccountSuspensionSetupAction() throws Exception {
        idamEventProcessor.accountSuspensionSetup(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleAccountSuspensionUpdatedAction() throws Exception {
        idamEventProcessor.accountSuspensionUpdated(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleDeregisterUserAction() throws Exception {
        idamEventProcessor.deregisterUserAccount(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleReregisterUserAction() throws Exception {
        idamEventProcessor.reregisterUserAccount(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleAccountDeletedAction() throws Exception {
        idamEventProcessor.accountDeleted(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleAccountEnrolmentChangedAction() throws Exception {
        idamEventProcessor.accountEnrolmentChanged(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleCreateOrganisationAction() throws Exception {
        idamEventProcessor.createOrganisation(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleUpdateOrganisationAction() throws Exception {
        idamEventProcessor.updateOrganisation(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleOrganisationDeregisteredAction() throws Exception {
        idamEventProcessor.organisationDeregistered(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleOrganisationReregistered() throws Exception {
        idamEventProcessor.organisationReregistered(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleOrganisationDeletedAction() throws Exception {
        idamEventProcessor.organisationDeleted(envelope);
        assertThatMessageSent();
    }

    @Test
    public void shouldHandleOrganisationSynchronisationAction() throws Exception {
        idamEventProcessor.organisationSynchronisation(envelope);
        assertThatMessageSent();
    }

    public void assertThatMessageSent() {
        verify(enveloper).withMetadataFrom(eq(envelope), anyString());
        ArgumentCaptor<JsonEnvelope> argumentCaptor = ArgumentCaptor.forClass(JsonEnvelope.class);
        verify(sender, times(1)).send(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().metadata(), is(metadata));
        assertThat(argumentCaptor.getValue().payload(), is(jsonValue));
    }
}