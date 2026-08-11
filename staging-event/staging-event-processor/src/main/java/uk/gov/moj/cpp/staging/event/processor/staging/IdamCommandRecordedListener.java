package uk.gov.moj.cpp.staging.event.processor.staging;

import uk.gov.justice.services.core.annotation.Component;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.moj.cpp.staging.event.processor.staging.helper.IdamEventProcessorHelper;
import uk.gov.moj.cpp.staging.event.processor.staging.helper.OrganisationCreatedIdamEventHelper;
import uk.gov.moj.cpp.staging.event.processor.staging.helper.OrganisationUpdatedIdamEventHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceComponent(Component.EVENT_PROCESSOR)
public class IdamCommandRecordedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdamCommandRecordedListener.class);

    @Inject
    IdamEventProcessorHelper idamEventProcessorHelper;

    @Inject
    private OrganisationCreatedIdamEventHelper organisationCreatedIdamEventHelper;

    @Inject
    private OrganisationUpdatedIdamEventHelper organisationUpdatedIdamEventHelper;

    private Map<String, Consumer<JsonEnvelope>> handlers;

    @PostConstruct
    public void initialize() {
        handlers = new HashMap<>();
        handlers.put("identity.events.account-enrolment-changed", idamEventProcessorHelper::handleEnrolmentChangedEvent);
        handlers.put("identity.events.account-updated", idamEventProcessorHelper::handleAccountUpdatedEvent);
        handlers.put("identity.events.account-deleted", idamEventProcessorHelper::handleUserDeletedEvent);
        handlers.put("identity.events.account-deregistered", idamEventProcessorHelper::handleUserDeregisteredEvent);
        handlers.put("identity.events.account-reregistered", idamEventProcessorHelper::handleUserReregisteredEvent);
        handlers.put("identity.events.organisation-created", organisationCreatedIdamEventHelper::processOrganisationCreated);
        handlers.put("identity.events.organisation-updated", organisationUpdatedIdamEventHelper::processOrganisationUpdated);
    }

    @Handles("staging.record-idam-command")
    public void handle(final JsonEnvelope event) {
        String name = event.payloadAsJsonObject()
                .getJsonObject("idamEvent")
                .getJsonObject("_metadata")
                .getString("name");

        Consumer<JsonEnvelope> handler = handlers.get(name);

        if (handler != null) {
            handler.accept(event);
        } else {
            LOGGER.info("Received unsupported IDAM event media type: {}", name);
        }
    }
}
