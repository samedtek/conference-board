package com.samedtek.conferenceboard.validation;


import com.samedtek.conferenceboard.model.payload.ConferencePayload;
import com.samedtek.conferenceboard.utils.Constants;
import com.samedtek.conferenceboard.utils.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.CollectionUtils;


public class ConferencePayloadValidator implements ConstraintValidator<ValidConferencePayload, ConferencePayload> {


    @Override
    public void initialize(ValidConferencePayload constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ConferencePayload conferencePayload, ConstraintValidatorContext context) {
        boolean valid = true;
        context.disableDefaultConstraintViolation();
        if (CollectionUtils.isEmpty(conferencePayload.getPresentationInfos())) {
            valid = false;
            context.buildConstraintViolationWithTemplate(ValidationMessages.EMPTY_PRESENTATION_LIST_EXCEPTION)
                    .addConstraintViolation();
        }
        for (String conferenceInfo : conferencePayload.getPresentationInfos()) {
            if (conferenceInfo.isBlank()) {
                valid = false;
                context.buildConstraintViolationWithTemplate(ValidationMessages.BLANK_PRESENTATION_TITLE_EXCEPTION).addConstraintViolation();
            }
            if (conferenceInfo.lastIndexOf(Constants.SPACE) == -1) {
                valid = false;
                context.buildConstraintViolationWithTemplate(ValidationMessages.PRESENTATION_MISSING_INFO_EXCEPTION).addConstraintViolation();
            }
            String durationText = conferenceInfo.substring(conferenceInfo.lastIndexOf(Constants.SPACE) + 1);
            int duration;
            if (!durationText.equalsIgnoreCase(Constants.LIGHTNING)) {
                try {
                    duration = Integer.parseInt(durationText.replace(Constants.MIN, ""));
                    if (duration > 180) {
                        valid = false;
                        context.buildConstraintViolationWithTemplate(ValidationMessages.PRESENTATION_DURATION_LONG_EXCEPTION).addConstraintViolation();
                    }
                    if (duration <= Constants.LIGHTNING_DURATION) {
                        valid = false;
                        context.buildConstraintViolationWithTemplate(ValidationMessages.PRESENTATION_DURATION_SHORT_EXCEPTION).addConstraintViolation();
                    }
                } catch (NumberFormatException numberFormatException) {
                    context.buildConstraintViolationWithTemplate(ValidationMessages.PRESENTATION_DURATION_FORMAT_EXCEPTION).addConstraintViolation();
                    return false;
                }
            }
        }
        return valid;
    }
}
