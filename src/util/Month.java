package util;

public enum Month {
    JANUARY(0, "January", "Jan"),
    FEBRUARY(1, "February", "Feb"),
    MARCH(2, "March", "Mar"),
    APRIL(3, "April", "Apr"),
    MAY(4, "May", "May"),
    JUNE(5, "June", "Jun"),
    JULY(6, "July", "Jul"),
    AUGUST(7, "August", "Aug"),
    SEPTEMBER(8, "September", "Sep"),
    OCTOBER(9, "October", "Oct"),
    NOVEMBER(10, "November", "Nov"),
    DECEMBER(11, "December", "Dec");

    private int index;
    private String name;
    private String abbreviation;

    Month(int index, String name, String abbreviation) {
        this.index = index;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public int getMonthIndex() {
        return index;
    }

    public String getMonthName() {
        return name;
    }

    public String getMonthAbbreviation() {
        return abbreviation;
    }

    public static Month getMonth(int index) {
        switch (index) {
            case 0:
                return JANUARY;
            case 1:
                return FEBRUARY;
            case 2:
                return MARCH;
            case 3:
                return APRIL;
            case 4:
                return MAY;
            case 5:
                return JUNE;
            case 6:
                return JULY;
            case 7:
                return AUGUST;
            case 8:
                return SEPTEMBER;
            case 9:
                return OCTOBER;
            case 10:
                return NOVEMBER;
            case 11:
                return DECEMBER;
            default:
                return null;
        }
    }
}