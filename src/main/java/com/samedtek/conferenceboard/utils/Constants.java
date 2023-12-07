package com.samedtek.conferenceboard.utils;

import java.text.SimpleDateFormat;

public final class Constants {

    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("hh:mma");
    public static final String SPACE = " ";
    public static final String HYPHEN = " - ";
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String MIN = "min";
    public static final String LIGHTNING = "lightning";
    public static final String TRACK = "Track ";
    public static final String BEFORE_LAUNCH_START = "09:00AM";
    public static final String AFTER_LAUNCH_START = "01:00PM";
    public static final String LAUNCH_TIME = "12:00PM";
    public static final String NETWORK_START_TIME = "04:00PM";
    public static final String END_TIME = "05:00PM";
    public static final String LAUNCH_TITLE = "Launch";
    public static final String NETWORK_EVENT_TITLE = "Networking Event";
    public static final int BEFORE_LAUNCH = 180;
    public static final int AFTER_LAUNCH = 240;
    public static final int LAUNCH_DURATION = 60;
    public static final int LIGHTNING_DURATION = 5;

}
