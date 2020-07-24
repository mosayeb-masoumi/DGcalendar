package calendar.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RawRes;

import com.example.coroutine.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;

import calendar.AbstractDate;
import calendar.CivilDate;
import calendar.DateConverter;
import calendar.IslamicDate;
import calendar.PersianDate;
import calendar.entity.AbstractEvent;
import calendar.entity.DeviceCalendarEvent;
import calendar.entity.GregorianCalendarEvent;
import calendar.entity.IslamicCalendarEvent;
import calendar.entity.PersianCalendarEvent;

import static calendar.util.Constants.PREF_HOLIDAY_TYPES;

public class Util {

    static private final String TAG = Util.class.getName();
    static private boolean iranTime = Constants.DEFAULT_IRAN_TIME;
    static private String islamicOffset = Constants.DEFAULT_ISLAMIC_OFFSET;
    static private String[] persianMonths;
    static private String[] islamicMonths;
    static private String[] gregorianMonths;
    static private String[] weekDays;
    static private String[] weekDaysInitials;
    static private int weekStartOffset;
    static private com.dadehgostar.tazkereh.enums.CalendarTypeEnum mainCalendar;

    static private SparseArray<List<PersianCalendarEvent>> persianCalendarEvents;
    static private SparseArray<List<IslamicCalendarEvent>> islamicCalendarEvents;
    static private SparseArray<List<GregorianCalendarEvent>> gregorianCalendarEvents;

    static public void initUtils(Context context) {
        updateStoredPreference(context);
        loadLanguageResource(context);
        loadEvents(context);
    }

    public Util(Context context) {
        updateStoredPreference(context);
        loadLanguageResource(context);
        loadEvents(context);
    }

    public static void setStatusBarTranslucent(Activity a) {

        a. getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = a.getWindow();

            w.setFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    static public long getTodayJdn() {
        return DateConverter.civilToJdn(new CivilDate(makeCalendarFromDate(new Date())));
    }

    static public Calendar makeCalendarFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (iranTime) {
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));
        }
        calendar.setTime(date);
        return calendar;
    }

    static public boolean isIranTime() {
        return iranTime;
    }

    static public String formatDeviceCalendarEventTitle(DeviceCalendarEvent event) {
        String desc = event.getDescription();
        String title = event.getTitle();
        if (!TextUtils.isEmpty(desc))
            title += " (" + event.getDescription() + ")";

        return title.replaceAll("\\n", " ").trim();
    }

    static public void updateStoredPreference(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        islamicOffset = prefs.getString(Constants.PREF_ISLAMIC_OFFSET, Constants.DEFAULT_ISLAMIC_OFFSET);
        iranTime = prefs.getBoolean(Constants.PREF_IRAN_TIME, Constants.DEFAULT_IRAN_TIME);
        mainCalendar = com.dadehgostar.tazkereh.enums.CalendarTypeEnum.valueOf(prefs.getString("mainCalendarType", "SHAMSI"));
        weekStartOffset = Integer.parseInt(prefs.getString("WeekStart", "0"));


    }

    static public String getWeekDayName(AbstractDate date) {
        if (date instanceof IslamicDate)
            date = DateConverter.islamicToCivil((IslamicDate) date);
        else if (date instanceof PersianDate)
            date = DateConverter.persianToCivil((PersianDate) date);

        return weekDays[date.getDayOfWeek() % 7];


    }

    public static String getInitialOfWeekDay(int position) {
        return weekDaysInitials[position % 7];
    }

    static public int fixDayOfWeek(int dayOfWeek) {
        return (dayOfWeek + weekStartOffset) % 7;
    }

    static private void loadLanguageResource(Context context) {
        @RawRes int messagesFile;
        messagesFile = R.raw.messages_fa;


        persianMonths = new String[12];
        islamicMonths = new String[12];
        gregorianMonths = new String[12];
        weekDays = new String[7];
        weekDaysInitials = new String[7];

        try {
            JSONObject messages = new JSONObject(readRawResource(context, messagesFile));

            JSONArray persianMonthsArray = messages.getJSONArray("PersianCalendarMonths");
            for (int i = 0; i < 12; ++i)
                persianMonths[i] = persianMonthsArray.getString(i);

            JSONArray islamicMonthsArray = messages.getJSONArray("IslamicCalendarMonths");
            for (int i = 0; i < 12; ++i)
                islamicMonths[i] = islamicMonthsArray.getString(i);

            JSONArray gregorianMonthsArray = messages.getJSONArray("GregorianCalendarMonths");
            for (int i = 0; i < 12; ++i)
                gregorianMonths[i] = gregorianMonthsArray.getString(i);

            JSONArray weekDaysArray = messages.getJSONArray("WeekDays");
            for (int i = 0; i < 7; ++i) {
                weekDays[i] = weekDaysArray.getString(i);
                weekDaysInitials[i] = weekDays[i].substring(0, 1);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    static private String[] monthsNamesOfCalendar(AbstractDate date) {
        if (date instanceof PersianDate)
            return persianMonths;
        else if (date instanceof IslamicDate)
            return islamicMonths;
        else
            return gregorianMonths;
    }

    static public String getMonthName(AbstractDate date) {
        return monthsNamesOfCalendar(date)[date.getMonth() - 1];
    }

    static private String readStream(InputStream is) {

        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static public String readRawResource(Context context, @RawRes int res) {
        return readStream(context.getResources().openRawResource(res));
    }

    static public int getIslamicOffset() {
        return Integer.parseInt(islamicOffset.replace("+", ""));
    }

    static public com.dadehgostar.tazkereh.enums.CalendarTypeEnum getMainCalendar() {
        return mainCalendar;
    }

    static public AbstractDate getTodayOfCalendar(com.dadehgostar.tazkereh.enums.CalendarTypeEnum calendar) {

        return getToday();

    }

    static public PersianDate getToday() {
        return DateConverter.civilToPersian(new CivilDate(makeCalendarFromDate(new Date())));
    }

    static public int fixDayOfWeekReverse(int dayOfWeek) {
        return (dayOfWeek + 7 - weekStartOffset) % 7;
    }

    static public AbstractDate getDateOfCalendar(com.dadehgostar.tazkereh.enums.CalendarTypeEnum calendar, int year, int month, int day) {
        switch (calendar) {
            case ISLAMIC:
                return new IslamicDate(year, month, day);
            case GREGORIAN:
                return new CivilDate(year, month, day);
            case SHAMSI:
            default:
                return new PersianDate(year, month, day);
        }
    }

    static public long getJdnOfCalendar(com.dadehgostar.tazkereh.enums.CalendarTypeEnum calendar, int year, int month, int day) {
        switch (calendar) {
            case ISLAMIC:
                return DateConverter.islamicToJdn(year, month, day);
            case GREGORIAN:
                return DateConverter.civilToJdn(year, month, day);
            case SHAMSI:
            default:
                return DateConverter.persianToJdn(year, month, day);
        }
    }

    static public int getDayOfWeekFromJdn(long jdn) {
        return DateConverter.jdnToCivil(jdn).getDayOfWeek() % 7;
    }

    static public long getJdnDate(AbstractDate date) {
        if (date instanceof PersianDate) {
            return DateConverter.persianToJdn((PersianDate) date);
        } else if (date instanceof IslamicDate) {
            return DateConverter.islamicToJdn((IslamicDate) date);
        } else if (date instanceof CivilDate) {
            return DateConverter.civilToJdn((CivilDate) date);
        } else {
            return 0;
        }
    }

    static public String getEventsTitle(List<AbstractEvent> dayEvents, boolean holiday,
                                        boolean compact, boolean showDeviceCalendarEvents,
                                        boolean insertRLM) {
        StringBuilder titles = new StringBuilder();
        boolean first = true;

        for (AbstractEvent event : dayEvents)
            if (event.isHoliday() == holiday) {
                String title = event.getTitle();
                if (insertRLM) {
                    title = Constants.RLM + title;
                }
                if (event instanceof DeviceCalendarEvent) {
                    if (!showDeviceCalendarEvents)
                        continue;

                    if (!compact) {
                        title = formatDeviceCalendarEventTitle((DeviceCalendarEvent) event);
                    }
                } else {
                    if (compact)
                        title = title.replaceAll(" \\(.*$", "");
                }

                if (first)
                    first = false;
                else
                    titles.append("\n");
                titles.append(title);
            }

        return titles.toString();
    }

    static public List<AbstractEvent> getEvents(long jdn) {
        PersianDate day = DateConverter.jdnToPersian(jdn);
        CivilDate civil = DateConverter.jdnToCivil(jdn);
        IslamicDate islamic = DateConverter.jdnToIslamic(jdn);

        List<AbstractEvent> result = new ArrayList<>();

        List<PersianCalendarEvent> persianList =
                persianCalendarEvents.get(day.getMonth() * 100 + day.getDayOfMonth());
        if (persianList != null)
            for (PersianCalendarEvent persianCalendarEvent : persianList)
                if (persianCalendarEvent.getDate().equals(day))
                    result.add(persianCalendarEvent);

        List<IslamicCalendarEvent> islamicList =
                islamicCalendarEvents.get(islamic.getMonth() * 100 + islamic.getDayOfMonth());
        if (islamicList != null)
            for (IslamicCalendarEvent islamicCalendarEvent : islamicList)
                if (islamicCalendarEvent.getDate().equals(islamic))
                    result.add(islamicCalendarEvent);

        List<GregorianCalendarEvent> gregorianList =
                gregorianCalendarEvents.get(civil.getMonth() * 100 + civil.getDayOfMonth());
        if (gregorianList != null)
            for (GregorianCalendarEvent gregorianCalendarEvent : gregorianList)
                if (gregorianCalendarEvent.getDate().equals(civil))
                    result.add(gregorianCalendarEvent);

     /*   List<DeviceCalendarEvent> deviceEventList =
                deviceCalendarEvents.get(civil.getMonth() * 100 + civil.getDayOfMonth());
        if (deviceEventList != null)
            for (DeviceCalendarEvent deviceCalendarEvent : deviceEventList)
                if (deviceCalendarEvent.getCivilDate().equals(civil))
                    result.add(deviceCalendarEvent);*/

        return result;
    }

    static private void loadEvents(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> enabledTypes = prefs.getStringSet(PREF_HOLIDAY_TYPES, new HashSet<String>());
        for(int i=0;i<5;i++){
            // if(prefs.getInt("Hol"+i,-1)==0){
            if(i==0)
                enabledTypes.add("iran_holidays");
            if(i==1)
                enabledTypes.add("iran_islamic");
            //  if(i==2)
            //  enabledTypes.add("iran_ancient");
            if(i==3)
                enabledTypes.add("iran_others");
            if(i==4)
                enabledTypes.add("international");
            //  }
        }
        if (enabledTypes.isEmpty()) {
            enabledTypes = new HashSet<>(Arrays.asList("iran_holidays"));

        }

        boolean afghanistanHolidays = enabledTypes.contains("afghanistan_holidays");
        boolean afghanistanOthers = enabledTypes.contains("afghanistan_others");
        boolean iranHolidays = enabledTypes.contains("iran_holidays");
        boolean iranIslamic = enabledTypes.contains("iran_islamic");
        boolean iranAncient = enabledTypes.contains("iran_ancient");
        boolean iranOthers = enabledTypes.contains("iran_others");
        boolean international =enabledTypes.contains("international");

        SparseArray<List<PersianCalendarEvent>> persianCalendarEvents = new SparseArray<>();
        SparseArray<List<IslamicCalendarEvent>> islamicCalendarEvents = new SparseArray<>();
        SparseArray<List<GregorianCalendarEvent>> gregorianCalendarEvents = new SparseArray<>();
        ArrayList<Object> allEnabledEvents = new ArrayList<>();
        ArrayList<String> allEnabledEventsTitles = new ArrayList<>();

        try {
            JSONArray days;
            int length;
            JSONObject allTheEvents = new JSONObject(readRawResource(context, R.raw.events));

            days = allTheEvents.getJSONArray("Persian Calendar");
            length = days.length();
            for (int i = 0; i < length; ++i) {
                JSONObject event = days.getJSONObject(i);

                int month = event.getInt("month");
                int day = event.getInt("day");
                int year = event.has("year") ? event.getInt("year") : -1;
                String title = event.getString("title");
                boolean holiday = event.getBoolean("holiday");

                boolean addOrNot = false;
                String type = event.getString("type");

                if (holiday && iranHolidays && (type.equals("Islamic Iran") ||
                        type.equals("Iran") || type.equals("Ancient Iran")))
                    addOrNot = true;

                if (!iranHolidays && type.equals("Islamic Iran"))
                    holiday = false;

                if (iranIslamic && type.equals("Islamic Iran"))
                    addOrNot = true;

                if (iranAncient && type.equals("Ancient Iran"))
                    addOrNot = true;

                if (iranOthers && type.equals("Iran"))
                    addOrNot = true;

                if (afghanistanHolidays && type.equals("Afghanistan") && holiday)
                    addOrNot = true;

                if (!afghanistanHolidays && type.equals("Afghanistan"))
                    holiday = false;

                if (afghanistanOthers && type.equals("Afghanistan"))
                    addOrNot = true;

                if (addOrNot) {
                    title += " (";
                    if (holiday && afghanistanHolidays && iranHolidays) {
                        if (type.equals("Islamic Iran") || type.equals("Iran"))
                            title += "ایران، ";
                        else if (type.equals("Afghanistan"))
                            title += "افغانستان، ";
                    }
                    title += (day) + " " + persianMonths[month - 1] + ")";

                    List<PersianCalendarEvent> list = persianCalendarEvents.get(month * 100 + day);
                    if (list == null) {
                        list = new ArrayList<>();
                        persianCalendarEvents.put(month * 100 + day, list);
                    }
                    PersianCalendarEvent ev = new PersianCalendarEvent(new PersianDate(year, month, day), title, holiday);
                    list.add(ev);
                    allEnabledEvents.add(ev);
                    allEnabledEventsTitles.add(title);
                }
            }

            days = allTheEvents.getJSONArray("Hijri Calendar");
            length = days.length();
            for (int i = 0; i < length; ++i) {
                JSONObject event = days.getJSONObject(i);

                int month = event.getInt("month");
                int day = event.getInt("day");
                String title = event.getString("title");
                boolean holiday = event.getBoolean("holiday");

                boolean addOrNot = false;
                String type = event.getString("type");

                if (afghanistanHolidays && holiday && type.equals("Islamic Afghanistan"))
                    addOrNot = true;

                if (!afghanistanHolidays && type.equals("Islamic Afghanistan"))
                    holiday = false;

                if (afghanistanOthers && type.equals("Islamic Afghanistan"))
                    addOrNot = true;

                if (iranHolidays && holiday && type.equals("Islamic Iran"))
                    addOrNot = true;

                if (!iranHolidays && type.equals("Islamic Iran"))
                    holiday = false;

                if (iranOthers && type.equals("Islamic Iran"))
                    addOrNot = true;

                if (addOrNot) {
                    title += " (";
                    if (holiday && afghanistanHolidays && iranHolidays) {
                        if (type.equals("Islamic Iran"))
                            title += "ایران، ";
                        else if (type.equals("Islamic Afghanistan"))
                            title += "افغانستان، ";
                    }
                    title += (day) + " " + islamicMonths[month - 1] + ")";
                    List<IslamicCalendarEvent> list = islamicCalendarEvents.get(month * 100 + day);
                    if (list == null) {
                        list = new ArrayList<>();
                        islamicCalendarEvents.put(month * 100 + day, list);
                    }
                    IslamicCalendarEvent ev = new IslamicCalendarEvent(new IslamicDate(-1, month, day), title, holiday);
                    list.add(ev);
                    allEnabledEvents.add(ev);
                    allEnabledEventsTitles.add(title);
                }
            }

            days = allTheEvents.getJSONArray("Gregorian Calendar");
            length = days.length();
            for (int i = 0; i < length; ++i) {
                JSONObject event = days.getJSONObject(i);

                int month = event.getInt("month");
                int day = event.getInt("day");
                String title = event.getString("title");

                if (international) {
                    title += " (" +(day) + " " + gregorianMonths[month - 1] + ")";
                    List<GregorianCalendarEvent> list = gregorianCalendarEvents.get(month * 100 + day);
                    if (list == null) {
                        list = new ArrayList<>();
                        gregorianCalendarEvents.put(month * 100 + day, list);
                    }
                    GregorianCalendarEvent ev = new GregorianCalendarEvent(new CivilDate(-1, month, day), title, false);
                    list.add(ev);
                    allEnabledEvents.add(ev);
                    allEnabledEventsTitles.add(title);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        Util.persianCalendarEvents = persianCalendarEvents;
        Util.islamicCalendarEvents = islamicCalendarEvents;
        Util.gregorianCalendarEvents = gregorianCalendarEvents;
     /*   Util.allEnabledEvents = allEnabledEvents;
        Util.allEnabledEventsTitles = allEnabledEventsTitles;*/

        //   readDeviceCalendarEvents(context);
    }
}
