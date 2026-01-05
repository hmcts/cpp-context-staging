package uk.gov.moj.cpp.staging.command.handler;

import static uk.gov.justice.services.core.annotation.Component.COMMAND_HANDLER;
import static uk.gov.moj.cpp.staging.domain.IdamStreamId.IDAM_STREAM_ID;

import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.eventsourcing.source.core.EventSource;
import uk.gov.justice.services.eventsourcing.source.core.exception.EventStreamException;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.staging.event.IdamEventRecorded;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.json.JsonObject;

@ServiceComponent(COMMAND_HANDLER)
public class IdamEventCommandHandler {

    @Inject
    private EventSource eventSource;

    @Inject
    private Enveloper enveloper;

    @Handles("staging.record-idam-command")
    public void recordIdamEvent(final JsonEnvelope command) throws EventStreamException {
        JsonObject idamEvent = command.payloadAsJsonObject().getJsonObject("idamEvent");

        if (null != idamEvent) {
            eventSource.getStreamById(IDAM_STREAM_ID).append(Stream.of(enveloper.withMetadataFrom(command)
                    .apply(new IdamEventRecorded(idamEvent))));
        }

    }
}
