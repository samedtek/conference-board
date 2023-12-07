package com.samedtek.conferenceboard.validation;

import com.samedtek.conferenceboard.utils.ValidationMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {ConferencePayloadValidator.class})
public @interface ValidConferencePayload {

    String message() default ValidationMessages.DEFAULT;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
