package util;

public enum TimeZone {
    UTC("UTC", "UTC", "+0000"),
    GMT("GMT", "GMT", "+0000"),
    EST("EST", "Eastern Standard Time", "-0500");

    private String abbreviation;
    private String name;
    private String offset;

    TimeZone(String abbreviation, String name, String offset) {
        this.abbreviation = abbreviation;
        this.name = name;
        this.offset = offset;
    }

    public String getTimeZoneName() {
        return name;
    }

    public String getTimeZoneAbbreviation() {
        return abbreviation;
    }

    public String getTimeZoneOffset() {
        return offset;
    }

    public long getTimeZoneOffsetInMilliseconds() {
        int isNegative = offset.charAt(0) == '-' ? -1 : 1;
        long hours = Long.parseLong(offset.substring(1, 3));
        long minutes = Long.parseLong(offset.substring(3));
        return isNegative * (hours * 3600000 + minutes * 60000);
    }

    public static TimeZone getTimeZone(String timeZoneAbbreviation) {
        for (TimeZone timeZone : TimeZone.values()) {
            if (timeZone.getTimeZoneAbbreviation().equals(timeZoneAbbreviation)) {
                return timeZone;
            }
        }
        return null;
    }
}