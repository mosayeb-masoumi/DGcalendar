package calendar.entity;

import calendar.PersianDate;


public class PersianCalendarEvent extends AbstractEvent {
    private PersianDate date;

    public PersianCalendarEvent(PersianDate date, String title, boolean holiday) {
        this.date = date;
        this.title = title;
        this.holiday = holiday;
    }

    public PersianDate getDate() {
        return date;
    }
}