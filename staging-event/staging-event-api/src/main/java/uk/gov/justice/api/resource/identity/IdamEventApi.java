package uk.gov.justice.api.resource.identity;

import static uk.gov.justice.services.core.annotation.Component.EVENT_API;

import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;

import jakarta.inject.Inject;

@ServiceComponent(EVENT_API)
public class IdamEventApi {

    @Inject
    private Sender sender;

    @Handles("identity.events.account-created")
    public void createUserAccount(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.account-updated")
    public void updateUserAccount(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.account-suspension-setup")
    public void accountSuspensionSetup(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.account-suspension-updated")
    public void accountSuspensionUpdated(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.account-deregistered")
    public void deregisterUserAccount(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.account-reregistered")
    public void reregisterUserAccount(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.account-deleted")
    public void accountDeleted(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.account-enrolment-changed")
    public void accountEnrolmentChanged(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.organisation-created")
    public void createOrganisation(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.organisation-updated")
    public void updateOrganisation(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.organisation-deregistered")
    public void organisationDeregistered(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.organisation-reregistered")
    public void organisationRegistered(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.organisation-deleted")
    public void organisationDeleted(final JsonEnvelope event) {
        sender.send(event);
    }

    @Handles("identity.events.organisation-synchronisation")
    public void organisationSynchronisation(final JsonEnvelope event) {
        sender.send(event);
    }

}