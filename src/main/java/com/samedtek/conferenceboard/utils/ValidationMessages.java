package com.samedtek.conferenceboard.utils;

public final class ValidationMessages {
    public static final String DEFAULT = "Presentation is not valid.";
    public static final String EMPTY_PRESENTATION_LIST_EXCEPTION = "Presentation List can not be empty.";
    public static final String BLANK_PRESENTATION_TITLE_EXCEPTION = "Presentation title can not be blank.";
    public static final String PRESENTATION_MISSING_INFO_EXCEPTION = "Presentation have include name and duration.";
    public static final String PRESENTATION_DURATION_LONG_EXCEPTION = "Presentation duration must be less then 180 minutes.";
    public static final String PRESENTATION_DURATION_SHORT_EXCEPTION = "Presentation duration must be greater then 5 minutes or must be Lightning.";
    public static final String PRESENTATION_DURATION_FORMAT_EXCEPTION = "Presentation duration must be Lightning or (Integer)min.";

}
