package uk.gov.moj.cpp.staging.event.processor.staging.helper;

import uk.gov.justice.services.core.annotation.Component;
import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.enveloper.Enveloper;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;

import jakarta.inject.Inject;
import uk.gov.justice.services.messaging.JsonObjects;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceComponent(Component.EVENT_PROCESSOR)
public class IdamEventProcessorHelper {

    public static final String CPP_SERVICE_NAME = "CPP";
    public static final String USER_ID = "userId";
    public static final String ORGANISATION_ID = "organisationId";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String SOURCE = "source";
    public static final String USER_TYPE = "userType";
    private static final Logger LOGGER = LoggerFactory.getLogger(IdamEventProcessorHelper.class);
    @Inject
    private Sender sender;

    @Inject
    private Enveloper enveloper;

    // TODO - Remove this and add @FrameworkComponent to just the sender
    @SuppressWarnings("squid:S1186")
    @Handles("some.thing.something")
    public void something(final JsonEnvelope event) {
    }

    public void handleEnrolmentChangedEvent(final JsonEnvelope event) {
        if (isEnrolmentForCpp(event)) {
            send(event, "usersgroups.set-user-details", buildUserDetailsForCommand(event));
        } else {
            LOGGER.info("Received enrolement for non-CPP service");
        }
    }

    public void handleUserDeletedEvent(final JsonEnvelope event) {
        send(event, "usersgroups.delete-user", buildAccountDeletedCommand(event));
    }

    public void handleUserReregisteredEvent(final JsonEnvelope event) {
        send(event, "usersgroups.reregister-user", buildRegisterDetailsForCommand(event));
    }

    public void handleUserDeregisteredEvent(final JsonEnvelope event) {
        send(event, "usersgroups.deregister-user", buildRegisterDetailsForCommand(event));
    }

    public void handleAccountUpdatedEvent(final JsonEnvelope event) {
        send(event, "usersgroups.set-user-details", buildUserDetailsForCommand(event));
    }

    private boolean isEnrolmentForCpp(JsonEnvelope event) {
        final JsonObject idamEventPayload = getPayload(event);
        return idamEventPayload.getJsonArray("idamServicesEnrolment").stream().map(obj -> ((JsonString) obj).getString()).anyMatch(service -> service.equals(CPP_SERVICE_NAME));
    }

    private JsonObject getPayload(final JsonEnvelope event) {
        return event.payloadAsJsonObject()
                .getJsonObject("idamEvent")
                .getJsonObject("payload");
    }

    private void send(final JsonEnvelope event, final String commandName, final JsonObject payload) {
        sender.send(enveloper.withMetadataFrom(event, commandName).apply(payload));
    }

    private JsonObject buildAccountDeletedCommand(final JsonEnvelope event) {
        final JsonObject idamEventPayload = getPayload(event);
        return JsonObjects.createObjectBuilder()
                .add(USER_ID, idamEventPayload.getString("idamId"))
                .build();
    }

    private JsonObject buildUserDetailsForCommand(final JsonEnvelope event) {
        final JsonObject idamEventPayload = getPayload(event);
        return JsonObjects.createObjectBuilder()
                .add(USER_ID, idamEventPayload.getString("idamId"))
                .add(ORGANISATION_ID, idamEventPayload.getString("idamOrgId"))
                .add(FIRST_NAME, idamEventPayload.getString(FIRST_NAME))
                .add(LAST_NAME, idamEventPayload.getString(LAST_NAME))
                .add(EMAIL, idamEventPayload.getString(EMAIL))
                .add(USER_TYPE, idamEventPayload.getString("idamAccountType"))
                .add(SOURCE, "IDAM")
                .build();
    }

    private JsonObject buildRegisterDetailsForCommand(final JsonEnvelope event) {
        final JsonObject idamEventPayload = getPayload(event);
        return JsonObjects.createObjectBuilder()
                .add(USER_ID, idamEventPayload.getString("idamId"))
                .add(ORGANISATION_ID, idamEventPayload.getString("idamOrgId"))
                .build();
    }

}