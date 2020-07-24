package calendar.entity;

import calendar.IslamicDate;


public class IslamicCalendarEvent extends AbstractEvent {
    private IslamicDate date;

    public IslamicCalendarEvent(IslamicDate date, String title, boolean holiday) {
        this.date = date;
        this.title = title;
        this.holiday = holiday;
    }

    public IslamicDate getDate() {
        return date;
    }
}