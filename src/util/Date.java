package util;
import java.util.*;

public class Date implements Comparable<Date> {
    protected int year;
    protected Month month;
    protected int day;
    protected int hour;
    protected int minute;
    protected int second;
    protected int millisecond;
    protected DayOfWeek dayOfWeek;
    protected int dayOfYear;
    protected long millisecondsUTC;

    protected TimeZone timeZone;

    public Date() {
        this(System.currentTimeMillis());
    }

    public Date(TimeZone timeZone) {
        this(System.currentTimeMillis(), timeZone);
    }

    public Date(long millisecondsUTC) {
        this(millisecondsUTC, TimeZone.UTC);
    }

    public Date(long millisecondsUTC, TimeZone timeZone) {
        parseDate(millisecondsUTC, timeZone);
    }

    public Date(String date) {
        parseDate(date);
    }

    private void parseDate(long millisecondsUTC, TimeZone timeZone) {
        this.millisecondsUTC = millisecondsUTC;
        this.timeZone = timeZone;
        long millisecondsLocal = millisecondsUTC + timeZone.getTimeZoneOffsetInMilliseconds();

        if (millisecondsLocal < 0) {
            parseDateNegative(millisecondsLocal);
        } else {
            parseDatePositive(millisecondsLocal);
        }

        dayOfWeek = calculateDayOfWeek();
    }

    /*
     * yyyy-MM-dd HH:mm:ss.SSS zzz
     */
    private void parseDate(String date) {
        year = Integer.parseInt(date.substring(0, 4));
        month = Month.getMonth(Integer.parseInt(date.substring(5, 7)) - 1);
        day = Integer.parseInt(date.substring(8, 10));
        hour = Integer.parseInt(date.substring(11, 13));
        minute = Integer.parseInt(date.substring(14, 16));
        second = Integer.parseInt(date.substring(17, 19));
        millisecond = Integer.parseInt(date.substring(20, 23));
        timeZone = TimeZone.getTimeZone(date.substring(24));

        millisecondsUTC = (long) millisecond + (second * 1000) + (minute * 60 * 1000) + (hour * 60 * 60 * 1000) + (dayOfYear * 24 * 60 * 60 * 1000) + ((year - 1970) * 365 * 24 * 60 * 60 * 1000);

        int[] daysInMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        if (isLeapYear(year)) {
            daysInMonth[1] = 29;
        }

        dayOfYear = 0;
        for (int i = 0; i < month.getMonthIndex(); i++) {
            dayOfYear += daysInMonth[i];
        }
        dayOfYear += day;

        dayOfWeek = calculateDayOfWeek();
    }

    public String format(String format) {
        String formattedDate = "";

        char lastChar = '‎';
        int charCount = 0;
        boolean inQuotes = false;
        boolean finalCharReached = false;

        List<Character> validChars = new LinkedList<>(Arrays.asList(' ', ',', ':', '-', '/', '.', '\'', '\"'));
        
        for (int i = 0; i < format.length() + 1; i++) {
            char currentChar = '‎';
            if (i != format.length()) {
                currentChar = format.charAt(i);
            }

            if (lastChar == '[') {
                inQuotes = true;
            } else if (lastChar == ']') {
                inQuotes = false;
            } else if (inQuotes) {
                formattedDate += lastChar;
            } else if (lastChar == '‎') {
                charCount++;
            } else {
                boolean repeatChar = (currentChar == lastChar);
                if (repeatChar) {
                    charCount++;
                } else if (finalCharReached || !repeatChar) {
                    if (!validChars.contains(lastChar)) {
                        switch (lastChar) {
                            case 'y': // year
                                String formattedYear = "y";
                                while (charCount > 1) {
                                    formattedYear += "y";
                                    charCount--;
                                }

                                if (formattedYear.equals("yy")) {
                                    formattedDate += String.valueOf(year).substring(2);
                                } else if (formattedYear.equals("yyyy")) {
                                    formattedDate += String.valueOf(year);
                                } else {
                                    throw new IllegalArgumentException("Invalid year format: \"" + formattedYear + "\"");
                                }
                                break;
                            case 'M': // month
                                String formattedMonth = "M";
                                while (charCount > 1) {
                                    formattedMonth += "M";
                                    charCount--;
                                }

                                if (formattedMonth.equals("M")) {
                                    formattedDate += String.valueOf(month.getMonthIndex() + 1);
                                } else if (formattedMonth.equals("MM")) {
                                    String formattedMonthValue = String.valueOf(month.getMonthIndex() + 1);
                                    if (formattedMonthValue.length() == 1) {
                                        formattedMonthValue = "0" + formattedMonthValue;
                                    }
                                    formattedDate += formattedMonthValue;
                                } else if (formattedMonth.equals("MMM")) {
                                    formattedDate += month.getMonthAbbreviation();
                                } else if (formattedMonth.equals("MMMM")) {
                                    formattedDate += month.getMonthName();
                                } else {
                                    throw new IllegalArgumentException("Invalid month format: \"" + formattedMonth + "\"");
                                }
                                break;
                            case 'd': // day
                                String formattedDay = "d";
                                while (charCount > 1) {
                                    formattedDay += "d";
                                    charCount--;
                                }

                                if (formattedDay.equals("d")) {
                                    formattedDate += String.valueOf(day);
                                } else if (formattedDay.equals("dd")) {
                                    String formattedDayValue = String.valueOf(day);
                                    if (formattedDayValue.length() == 1) {
                                        formattedDayValue = "0" + formattedDayValue;
                                    }
                                    formattedDate += formattedDayValue;
                                } else {
                                    throw new IllegalArgumentException("Invalid day format: \"" + formattedDay + "\"");
                                }
                                break;
                            case 'E': // day of week
                                String formattedDayOfWeek = "E";
                                while (charCount > 1) {
                                    formattedDayOfWeek += "E";
                                    charCount--;
                                }

                                if (formattedDayOfWeek.equals("EEE")) {
                                    formattedDate += dayOfWeek.getDayAbbreviation();
                                } else if (formattedDayOfWeek.equals("EEEE")) {
                                    formattedDate += dayOfWeek.getDayName();
                                } else {
                                    throw new IllegalArgumentException("Invalid day of week format: \"" + formattedDayOfWeek + "\"");
                                }
                                break;
                            case 'H': // hour (24)
                                String formattedHour24 = "H";
                                while (charCount > 1) {
                                    formattedHour24 += "H";
                                    charCount--;
                                }

                                if (formattedHour24.equals("H")) {
                                    formattedDate += String.valueOf(hour);
                                } else if (formattedHour24.equals("HH")) {
                                    String formattedHour24Value = String.valueOf(hour);
                                    if (formattedHour24Value.length() == 1) {
                                        formattedHour24Value = "0" + formattedHour24Value;
                                    }
                                    formattedDate += formattedHour24Value;
                                } else {
                                    throw new IllegalArgumentException("Invalid hour (24) format: \"" + formattedHour24 + "\"");
                                }
                                break;
                            case 'h': // hour (12)
                                String formattedHour12 = "h";
                                while (charCount > 1) {
                                    formattedHour12 += "h";
                                    charCount--;
                                }

                                int hour12 = hour % 12;
                                if (hour12 == 0) {
                                    hour12 = 12;
                                }

                                if (formattedHour12.equals("h")) {
                                    formattedDate += String.valueOf(hour12);
                                } else if (formattedHour12.equals("hh")) {
                                    String formattedHour12Value = String.valueOf(hour12);
                                    if (formattedHour12Value.length() == 1) {
                                        formattedHour12Value = "0" + formattedHour12Value;
                                    }
                                    formattedDate += formattedHour12Value;
                                } else {
                                    throw new IllegalArgumentException("Invalid hour (12) format: \"" + formattedHour12 + "\"");
                                }
                                break;
                            case 'm': // minute
                                String formattedMinute = "m";
                                while (charCount > 1) {
                                    formattedMinute += "m";
                                    charCount--;
                                }

                                if (formattedMinute.equals("m")) {
                                    formattedDate += String.valueOf(minute);
                                } else if (formattedMinute.equals("mm")) {
                                    String formattedMinuteValue = String.valueOf(minute);
                                    if (formattedMinuteValue.length() == 1) {
                                        formattedMinuteValue = "0" + formattedMinuteValue;
                                    }
                                    formattedDate += formattedMinuteValue;
                                } else {
                                    throw new IllegalArgumentException("Invalid minute format: \"" + formattedMinute + "\"");
                                }
                                break;
                            case 's': // second
                                String formattedSecond = "s";
                                while (charCount > 1) {
                                    formattedSecond += "s";
                                    charCount--;
                                }

                                if (formattedSecond.equals("s")) {
                                    formattedDate += String.valueOf(second);
                                } else if (formattedSecond.equals("ss")) {
                                    String formattedSecondValue = String.valueOf(second);
                                    if (formattedSecondValue.length() == 1) {
                                        formattedSecondValue = "0" + formattedSecondValue;
                                    }
                                    formattedDate += formattedSecondValue;
                                } else {
                                    throw new IllegalArgumentException("Invalid second format: \"" + formattedSecond + "\"");
                                }
                                break;
                            case 'S': // millisecond
                                String formattedMillisecond = "S";
                                while (charCount > 1) {
                                    formattedMillisecond += "S";
                                    charCount--;
                                }

                                if (formattedMillisecond.equals("S")) {
                                    if (millisecond >= 900) {
                                        formattedDate += "9";
                                    } else {
                                        formattedDate += String.valueOf(Math.round(millisecond / 100.0));
                                    }
                                } else if (formattedMillisecond.equals("SS")) {
                                    String formattedMillisecondValue = "";
                                    if (millisecond >= 990) {
                                        formattedMillisecondValue += "99";
                                    } else {
                                        formattedMillisecondValue += String.valueOf(Math.round(millisecond / 10.0));
                                    }
                                    if (formattedMillisecondValue.length() == 1) {
                                        formattedMillisecondValue = "0" + formattedMillisecondValue;
                                    }
                                    formattedDate += formattedMillisecondValue;
                                } else if (formattedMillisecond.equals("SSS")) {
                                    String formattedMillisecondValue = String.valueOf(millisecond);
                                    if (formattedMillisecondValue.length() == 1) {
                                        formattedMillisecondValue = "00" + formattedMillisecondValue;
                                    } else if (formattedMillisecondValue.length() == 2) {
                                        formattedMillisecondValue = "0" + formattedMillisecondValue;
                                    }
                                    formattedDate += formattedMillisecondValue;
                                } else {
                                    throw new IllegalArgumentException("Invalid millisecond format: \"" + formattedMillisecond + "\"");
                                }
                                break;
                            case 'z': // time zone
                                String formattedTimeZone = "z";
                                while (charCount > 1) {
                                    formattedTimeZone += "z";
                                    charCount--;
                                }

                                if (formattedTimeZone.equals("z") || formattedTimeZone.equals("zzz")) {
                                    formattedDate += timeZone.getTimeZoneAbbreviation();
                                } else if (formattedTimeZone.equals("zzzz")) {
                                    formattedDate += timeZone.getTimeZoneName();
                                } else {
                                    throw new IllegalArgumentException("Invalid time zone format: \"" + formattedTimeZone + "\"");
                                }
                                break;
                            case 'Z': // time zone offset
                                String formattedTimeZoneOffset = "Z";
                                while (charCount > 1) {
                                    formattedTimeZoneOffset += "Z";
                                    charCount--;
                                }

                                if (formattedTimeZoneOffset.equals("Z")) {
                                    formattedDate += "UTC" + timeZone.getTimeZoneOffset();
                                } else if (formattedTimeZoneOffset.equals("ZZ")) {
                                    formattedTimeZone = "UTC" + timeZone.getTimeZoneOffset();
                                    formattedDate += formattedTimeZone.substring(0, 3) + ":" + formattedTimeZone.substring(3);
                                } else {
                                    throw new IllegalArgumentException("Invalid time zone offset format: \"" + formattedTimeZoneOffset + "\"");
                                }
                                break;
                            case 'a': // meridiem
                                String formattedMeridiem = "a";
                                while (charCount > 1) {
                                    formattedMeridiem += "a";
                                    charCount--;
                                }

                                if (formattedMeridiem.equals("a")) {
                                    formattedDate += (hour < 12) ? "am" : "pm";
                                } else if (formattedMeridiem.equals("aa")) {
                                    formattedDate += (hour < 12) ? "AM" : "PM";
                                } else {
                                    throw new IllegalArgumentException("Invalid meridiem format: \"" + formattedMeridiem + "\"");
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid date format character: \'" + lastChar + "\'");
                            }
                        } else {
                            while (charCount > 0) {
                                formattedDate += lastChar;
                                charCount--;
                            }
                            lastChar = '‎';
                            charCount = 1;
                        }
                }
            }
            lastChar = currentChar;
        }

        return formattedDate;
    }

    public Date toUTCDate() {
        long millisecondsInDay = 86400000;

        long milliseconds = 0;
        for (int i = 1970; i < year; i++) {
            milliseconds += (isLeapYear(i)) ? (366 * millisecondsInDay) : (365 * millisecondsInDay);
        }

        milliseconds += (this.dayOfYear - 1) * millisecondsInDay;
        milliseconds += this.hour * 3600000;
        milliseconds += this.minute * 60000;
        milliseconds += this.second * 1000;
        milliseconds += this.millisecond;

        milliseconds -= this.timeZone.getTimeZoneOffsetInMilliseconds();

        return new Date(milliseconds);
    }
    
    public int getYear() {
        return this.year;
    }

    public Month getMonth() {
        return this.month;
    }

    public int getDay() {
        return this.day;
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public int getSecond() {
        return this.second;
    }

    public int getMillisecond() {
        return this.millisecond;
    }

    public long getMillisecondsUTC() {
        return this.millisecondsUTC;
    }

    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public DayOfWeek getDayOfWeek() {
        return this.dayOfWeek;
    }

    public int getDayOfYear() {
        return this.dayOfYear;
    }

    public String getKey() {
        return format("yyyy-MM-dd HH:mm:ss.SSS zzz");
    }

    public int compareTo(Date date) {
        if (date == null) {
            return -1;
        }

        Date thisDateUTC = toUTCDate();
        Date dateUTC = date.toUTCDate();

        int yearDifference = thisDateUTC.getYear() - dateUTC.getYear();
        if (yearDifference != 0) {
            return (int) Math.signum(yearDifference);
        } else {
            int monthDifference = thisDateUTC.getMonth().getMonthIndex() - dateUTC.getMonth().getMonthIndex();
            if (monthDifference != 0) {
                return (int) Math.signum(monthDifference);
            } else {
                int dayDifference = thisDateUTC.getDay() - dateUTC.getDay();
                if (dayDifference != 0) {
                    return (int) Math.signum(dayDifference);
                } else {
                    int hourDifference = thisDateUTC.getHour() - dateUTC.getHour();
                    if (hourDifference != 0) {
                        return (int) Math.signum(hourDifference);
                    } else {
                        int minuteDifference = thisDateUTC.getMinute() - dateUTC.getMinute();
                        if (minuteDifference != 0) {
                            return (int) Math.signum(minuteDifference);
                        } else {
                            int secondDifference = thisDateUTC.getSecond() - dateUTC.getSecond();
                            if (secondDifference != 0) {
                                return (int) Math.signum(secondDifference);
                            } else {
                                int millisecondDifference = thisDateUTC.getMillisecond() - dateUTC.getMillisecond();
                                return (int) Math.signum(millisecondDifference);
                            }
                        }
                    }
                }
            }
        }
    }

    public String toString() {
        return format("dd MMM yyyy [at] HH:mm:ss.SSS zzz");
    }

    private void parseDatePositive(long millisecondsLocal) {
        int[] daysInMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        int year = 1970;
        int monthIndex = 0;

        long days = millisecondsLocal / 86400000l;

        while (days >= 365) {
            if (isLeapYear(++year)) {
                days -= 366;
            } else {
                days -= 365;
            }
        }

        if (isLeapYear(year)) {
            daysInMonth[1] = 29;
            days++;
        }

        int dayInYear = (int) days;

        while (days >= daysInMonth[monthIndex]) {
            days -= daysInMonth[monthIndex];
            monthIndex++;
        }

        long day = days + 1;
        millisecondsLocal -= days * 86400000l;

        long hour = (millisecondsLocal / 3600000l) % 24;
        millisecondsLocal %= 3600000l;

        long minute = millisecondsLocal / 60000l;
        millisecondsLocal %= 60000l;

        long second = millisecondsLocal / 1000l;
        millisecondsLocal %= 1000l;

        this.year = year;
        this.month = Month.getMonth(monthIndex);
        this.day = (int) day;
        this.hour = (int) hour;
        this.minute = (int) minute;
        this.second = (int) second;
        this.millisecond = (int) millisecondsLocal;
        this.dayOfYear = dayInYear;
    }

    private void parseDateNegative(long millisecondsLocal) {
        parseDatePositive(-millisecondsLocal);

        dayOfYear = (isLeapYear(year) ? 366 : 365) - dayOfYear;
        long days = dayOfYear;

        int[] daysInMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        int monthIndex = 0;

        while (monthIndex < 12 && days >= daysInMonth[monthIndex]) {
            days -= daysInMonth[monthIndex];
            monthIndex++;
        }

        if (days == 0) {
            monthIndex--;
            days = daysInMonth[monthIndex];
        }

        day = (int) days;

        month = Month.getMonth(monthIndex);

        year = 1969 - (year - 1970);

        this.hour = 23 - (int) hour;
        this.minute = 59 - (int) minute;
        this.second = 59 - (int) second;
        this.millisecond = 1000 - (int) millisecond;

        if (this.millisecond == 1000) {
            this.millisecond = 0;
            this.second++;
        }
    }

    private DayOfWeek calculateDayOfWeek() {
        int century = year / 100;
        int shortYear = year % 100;

        int centuryCode;
        if (year == 1582) {
            if (month.getMonthIndex() == 9) {
                if (day <= 4) {
                    centuryCode = getJulianCenturyCode(century);
                } else if (day >= 15) {
                    centuryCode = getGregorianCenturyCode(century);
                } else {
                    return null;
                }
            } else if (month.getMonthIndex() > 9) {
                centuryCode = getGregorianCenturyCode(century);
            } else {
                centuryCode = getJulianCenturyCode(century);
            }
        } else {
            centuryCode = (year < 1583) ? getJulianCenturyCode(century) : getGregorianCenturyCode(century);
        }

        int yearCode = getYearCode(shortYear);

        int monthCodes[] = { 0, 3, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5 };
        int monthCode = monthCodes[month.getMonthIndex()];

        int leapYearCode = (isLeapYear(year) && (month.getMonthIndex() < 2)) ? 1 : 0;

        return DayOfWeek.getDayOfWeek((centuryCode + yearCode + monthCode + day - leapYearCode) % 7);
    }

    private int getJulianCenturyCode(int century) {
        return 18 - century;
    }

    private int getGregorianCenturyCode(int century) {
        return 2 * (3 - century % 4);
    }

    private int getYearCode(int shortYear) {
        return shortYear + (shortYear / 4);
    }

    private boolean isLeapYear(int year) {
        return (year < 1583) ? isJulianLeapYear(year) : isGregorianLeapYear(year);
    }

    private boolean isJulianLeapYear(int year) {
        return (year % 4 == 0);
    }

    private boolean isGregorianLeapYear(int year) {
        return (year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0);
    }
}