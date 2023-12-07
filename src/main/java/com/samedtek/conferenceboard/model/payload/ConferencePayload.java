package com.samedtek.conferenceboard.model.payload;

import com.samedtek.conferenceboard.validation.ValidConferencePayload;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ValidConferencePayload
public class ConferencePayload {

    private List<String> presentationInfos;

}
