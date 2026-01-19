package uk.gov.moj.cpp.staging.event.processor.identity;


import uk.gov.justice.services.core.annotation.Component;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;

import javax.inject.Inject;
import uk.gov.justice.services.messaging.JsonObjects;
import javax.json.JsonObject;

@ServiceComponent(Component.EVENT_PROCESSOR)
public class IdamEventProcessor {

    private static final String IDAM_COMMAND_NAME = "staging.record-idam-command";

    @Inject
    Sender sender;

    @Inject
    Enveloper enveloper;

    @Handles("identity.events.account-created")
    public void createUserAccount(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.account-updated")
    public void updateUserAccount(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.account-suspension-setup")
    public void accountSuspensionSetup(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.account-suspension-updated")
    public void accountSuspensionUpdated(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.account-deregistered")
    public void deregisterUserAccount(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.account-reregistered")
    public void reregisterUserAccount(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.account-deleted")
    public void accountDeleted(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.account-enrolment-changed")
    public void accountEnrolmentChanged(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.organisation-created")
    public void createOrganisation(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.organisation-updated")
    public void updateOrganisation(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.organisation-deregistered")
    public void organisationDeregistered(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.organisation-reregistered")
    public void organisationReregistered(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.organisation-deleted")
    public void organisationDeleted(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    @Handles("identity.events.organisation-synchronisation")
    public void organisationSynchronisation(final JsonEnvelope event) {
        sendIdamCommandToStagingApi(event);
    }

    private void sendIdamCommandToStagingApi(final JsonEnvelope event) {
        JsonObject idamEvent = JsonObjects.createObjectBuilder()
                .add("_metadata", event.metadata().asJsonObject())
                .add("payload", event.payloadAsJsonObject())
                .build();

        JsonObject payload = JsonObjects.createObjectBuilder()
                .add("idamEvent", idamEvent)
                .build();

        sender.send(enveloper.withMetadataFrom(event, IDAM_COMMAND_NAME).apply(payload));
    }
}
