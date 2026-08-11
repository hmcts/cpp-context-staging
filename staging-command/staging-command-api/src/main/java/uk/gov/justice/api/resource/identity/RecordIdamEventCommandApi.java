package uk.gov.justice.api.resource.identity;

import static uk.gov.justice.services.core.annotation.Component.COMMAND_API;

import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;

import jakarta.inject.Inject;

@ServiceComponent(COMMAND_API)
public class RecordIdamEventCommandApi {

    @Inject
    Sender sender;

    @Handles("staging.record-idam-command")
    public void recordIdamEvent(final JsonEnvelope command) {
        sender.send(command);
    }
}
