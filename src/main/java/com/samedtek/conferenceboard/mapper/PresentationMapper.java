package com.samedtek.conferenceboard.mapper;

import com.samedtek.conferenceboard.entitiy.Presentation;
import com.samedtek.conferenceboard.model.payload.ConferencePayload;
import com.samedtek.conferenceboard.utils.Constants;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PresentationMapper {

    default List<Presentation> mapConferencePayloadToPresentationList(ConferencePayload conferencePayload) {

        List<Presentation> presentations = new ArrayList<>();
        for (String presentationInfo : conferencePayload.getPresentationInfos()) {
            Presentation presentation = new Presentation();
            presentation.setTitle(presentationInfo.substring(0, presentationInfo.lastIndexOf(Constants.SPACE)));
            String presentationDuration = presentationInfo.substring(presentationInfo.lastIndexOf(Constants.SPACE) + 1)
                    .replace(Constants.MIN, "");
            if (presentationDuration.equalsIgnoreCase(Constants.LIGHTNING)) {
                presentation.setDuration(Constants.LAUNCH_DURATION);
            } else {
                presentation.setDuration(Integer.parseInt(presentationDuration));
            }
            presentations.add(presentation);
        }
        return presentations;
    }

}
