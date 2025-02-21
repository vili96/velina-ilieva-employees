package org.example.util;

public class Constants {

    public static final String CSV_DELIMITER = ",";
    public static final String CSV_FILE_PATH = "/files/input.csv";
    public static final int EXPECTED_COLUMNS = 4;
    public static final int MIN_PAIR_SIZE = 2;
    public static final String NULL_VALUE = "NULL";
    public static final String EMPTY_STRING = "";

    // ISO formats
    public static final String ISO_DATE = "yyyy-MM-dd";
    public static final String ISO_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String ISO_DATE_TIME_ZONE = "yyyy-MM-dd'T'HH:mm:ssX";
    public static final String ISO_INSTANT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    // Common European formats
    public static final String EU_DATE = "dd-MM-yyyy";
    public static final String EU_DATE_SLASH = "dd/MM/yyyy";
    public static final String EU_DATE_DOT = "dd.MM.yyyy";
    public static final String EU_DATE_SPACE = "dd MM yyyy";

    // US formats
    public static final String US_DATE = "MM-dd-yyyy";
    public static final String US_DATE_SLASH = "MM/dd/yyyy";
    public static final String US_DATE_DOT = "MM.dd.yyyy";
    public static final String US_DATE_SPACE = "MM dd yyyy";

    // Year first formats
    public static final String YEAR_FIRST_DASH = "yyyy-MM-dd";
    public static final String YEAR_FIRST_SLASH = "yyyy/MM/dd";
    public static final String YEAR_FIRST_DOT = "yyyy.MM.dd";
    public static final String YEAR_FIRST_SPACE = "yyyy MM dd";

    // Short year formats
    public static final String SHORT_YEAR_EU = "dd-MM-yy";
    public static final String SHORT_YEAR_US = "MM-dd-yy";
    public static final String SHORT_YEAR_FIRST = "yy-MM-dd";

    // Month name formats
    public static final String FULL_MONTH_NAME = "dd MMMM yyyy";
    public static final String SHORT_MONTH_NAME = "dd MMM yyyy";
    public static final String FULL_MONTH_NAME_COMMA = "MMMM dd, yyyy";
    public static final String SHORT_MONTH_NAME_COMMA = "MMM dd, yyyy";
}
