package util;

public enum DayOfWeek {
    SUNDAY(0, "Sunday", "Sun"),
    MONDAY(1, "Monday", "Mon"),
    TUESDAY(2, "Tuesday", "Tue"),
    WEDNESDAY(3, "Wednesday", "Wed"),
    THURSDAY(4, "Thursday", "Thu"),
    FRIDAY(5, "Friday", "Fri"),
    SATURDAY(6, "Saturday", "Sat");

    private final int index;
    private final String name;
    private final String abbreviation;

    DayOfWeek(int index, String name, String abbreviation) {
        this.index = index;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public int getDayIndex() {
        return index;
    }

    public String getDayName() {
        return name;
    }

    public String getDayAbbreviation() {
        return abbreviation;
    }

    public static DayOfWeek getDayOfWeek(int index) {
        switch (index) {
            case 0:
                return SUNDAY;
            case 1:
                return MONDAY;
            case 2:
                return TUESDAY;
            case 3:
                return WEDNESDAY;
            case 4:
                return THURSDAY;
            case 5:
                return FRIDAY;
            case 6:
                return SATURDAY;
            default:
                return null;
        }
    }
}