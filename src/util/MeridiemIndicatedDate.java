package util;

public class MeridiemIndicatedDate extends Date {
    protected int meridiem;

    public MeridiemIndicatedDate() {
        super();
        meridiem = (hour < 12) ? 0 : 1;
    }

    public MeridiemIndicatedDate(TimeZone timeZone) {
        super(timeZone);
        meridiem = (hour < 12) ? 0 : 1;
    }

    public MeridiemIndicatedDate(long millisecondsUTC) {
        super(millisecondsUTC, TimeZone.UTC);
        meridiem = (hour < 12) ? 0 : 1;
    }

    public MeridiemIndicatedDate(long millisecondsUTC, TimeZone timeZone) {
        super(millisecondsUTC, timeZone);
        meridiem = (hour < 12) ? 0 : 1;
    }

    public MeridiemIndicatedDate(String date) {
        super(date);
        meridiem = (hour < 12) ? 0 : 1;
    }

    public int getHour() {
        int hour12 = hour % 12;
        if (hour12 == 0) {
            hour12 = 12;
        }
        return hour12;
    }

    public String getMeridiem() {
        return (meridiem == 0) ? "AM" : "PM";
    }
}