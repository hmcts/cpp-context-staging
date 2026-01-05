package uk.gov.justice.api.resource.identity;

import static org.mockito.Mockito.verify;

import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;

import javax.json.JsonObject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RecordIdamEventTest {

    @Mock
    JsonEnvelope envelope;
    @Mock
    JsonObject payload;
    @Mock
    private Sender sender;
    @InjectMocks
    private RecordIdamEventCommandApi recordIdamEventCommandApi;

    @Test
    public void shouldRecordIdamEvent() throws Exception {
        recordIdamEventCommandApi.recordIdamEvent(envelope);
        verify(sender).send(envelope);
    }
}