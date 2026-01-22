package uk.gov.moj.cpp.staging.event.processor.staging.helper;

import uk.gov.justice.services.core.annotation.Component;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;

import javax.inject.Inject;
import uk.gov.justice.services.messaging.JsonObjects;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

@ServiceComponent(Component.EVENT_PROCESSOR)
public class OrganisationCreatedIdamEventHelper {

    @Inject
    private Sender sender;

    @Inject
    private Enveloper enveloper;

    @SuppressWarnings("squid:S1186")
    @Handles("some.thing.something.OrganisationCreatedIdamEventHelper")
    public void something(final JsonEnvelope event) {
    }

    public void processOrganisationCreated(final JsonEnvelope event) {
            send(event, "usersgroups.create-organisation-with-details", buildCreateOrganisationCommand(event));
    }

    private JsonObject getPayload(final JsonEnvelope event) {
        return event.payloadAsJsonObject()
                .getJsonObject("idamEvent")
                .getJsonObject("payload");
    }

    private void send(final JsonEnvelope event, final String commandName, final JsonObject payload) {
        sender.send(enveloper.withMetadataFrom(event, commandName).apply(payload));
    }

    private JsonObject buildCreateOrganisationCommand(final JsonEnvelope event) {
        final JsonObject idamEventPayload = getPayload(event);
        final JsonObjectBuilder builder = JsonObjects.createObjectBuilder();
        idamEventPayload.keySet().stream().filter(x -> !"idamOrganisationId".equalsIgnoreCase(x)).forEach(x -> builder.add(x, idamEventPayload.getString(x)));
        return builder.add("organisationId", idamEventPayload.getString("idamOrganisationId")).build();
    }
}