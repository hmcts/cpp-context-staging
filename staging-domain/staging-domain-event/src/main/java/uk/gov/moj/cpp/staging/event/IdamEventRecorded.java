package uk.gov.moj.cpp.staging.event;

import uk.gov.justice.domain.annotation.Event;

import jakarta.json.JsonObject;

@Event("staging.record-idam-command")
public class IdamEventRecorded {

    private final JsonObject idamEvent;

    public IdamEventRecorded(final JsonObject idamEvent) {
        this.idamEvent = idamEvent;
    }

    public JsonObject getIdamEvent() {
        return idamEvent;
    }

}