package com.samedtek.conferenceboard.service;

import com.samedtek.conferenceboard.model.payload.ConferencePayload;

public interface ConferenceBoardService {

    void scheduleConferenceBoard(ConferencePayload conferencePayload);

    String getConfereneBoard();

    void resetConferenceBoard();
}
