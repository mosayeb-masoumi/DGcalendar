package calendar.util;

public class Constants {
//    public static String ab = "/api/UserEvents/Event?eventId=24e7423e-f36b-1410-8e31-00f08822f0a5";
    public static final int CALENDAR_EVENT_ADD_MODIFY_REQUEST_CODE = 63;

    public static final boolean DEFAULT_IRAN_TIME = true;
    public static final String PREF_IRAN_TIME = "IranTime";
    public static final String PREF_GUEST_USER = "guest_user";
    public static final String DEFAULT_ISLAMIC_OFFSET = "0";
    public static final String PREF_ISLAMIC_OFFSET = "islamicOffset";

    public static final String OFFSET_ARGUMENT = "OFFSET_ARGUMENT";
    public static final String BROADCAST_INTENT_TO_MONTH_FRAGMENT = "BROADCAST_INTENT_TO_MONTH_FRAGMENT";
    public static final String BROADCAST_FIELD_TO_MONTH_FRAGMENT = "BROADCAST_FIELD_TO_MONTH_FRAGMENT";
    public static final String BROADCAST_FIELD_SELECT_DAY = "BROADCAST_FIELD_SELECT_DAY";
    public static int TODAY_DATE = 0;
    public static final char RLM = '\u200F';
    public static final String PREF_HOLIDAY_TYPES = "holiday_types";
    public static final String BROADCAST_RESTART_APP = "BROADCAST_RESTART_APP";
    public static final String CHOOSE_FRAGMENT = "choose_fragment";

    public static int  dayOfSelected;
    public static int  dayOfWeeklySelected;
    public static  boolean SELECT_WEEKLY_REMINDER = false;
    public static final String COUNT_REMINDER = "count_reminder";
    public static final String TOKEN_AUTHORIZATION = "token_authorization";

    public static final String EXTRA_ALARM_ID = "ALARM_ID";
    public static final String EXTRA_ALARM_ID_ = "ALARM_ID_";
    public static final String EXTRA_TITLE = "ALARM_TITLE";
    public static final String EXTRA_ALARM_DATE = "ALARM_DATE";
    public static final String EXTRA_TIME = "ALARM_TIME";
    public static final String EXTRA_REPEAT = "ALARM_REPEAT";
    public static final String EXTRA_REPORT = "ALARM_REPORT";
    public static final String EXTRA_ACTIVE = "ALARM_ACTIVE";

    public static final String[] REPEAT = new String[]{
            "unrepeat", "weekly", "monthly","weeklymonthly"
    };
    public static final String[] REPORT_AS = new String[]{"notif", "popup"
    };
}
