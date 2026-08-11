package uk.gov.justice.api.resource.identity;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IdamEventApiTest {

    @Mock
    JsonEnvelope envelope;

    @Mock
    JsonObject payload;

    @Mock
    private Sender sender;

    @Mock
    Enveloper enveloper;

    @Mock
    Metadata metadata;

    @Mock
    JsonValue jsonValue;

    @InjectMocks
    private IdamEventApi idamEventApi;

    @Test
    public void shouldHandleCreateUserAction() throws Exception {
        idamEventApi.createUserAccount(envelope);
        verify(sender, times(1)).send(envelope);
    }

    @Test
    public void shouldHandleUpdateUserAction() throws Exception {
        idamEventApi.updateUserAccount(envelope);
        verify(sender, times(1)).send(envelope);
    }

    @Test
    public void shouldHandleDeregisterUserAction() throws Exception {
        idamEventApi.deregisterUserAccount(envelope);
        verify(sender, times(1)).send(envelope);
    }

    @Test
    public void shouldHandleReregisterUserAction() throws Exception {
        idamEventApi.reregisterUserAccount(envelope);
        verify(sender, times(1)).send(envelope);
    }

    @Test
    public void shouldHandleCreateOrganisationAction() throws Exception {
        idamEventApi.createOrganisation(envelope);
        verify(sender, times(1)).send(envelope);
    }

    @Test
    public void shouldHandleUpdateOrganisationAction() throws Exception {
        idamEventApi.updateOrganisation(envelope);
        verify(sender, times(1)).send(envelope);
    }
}