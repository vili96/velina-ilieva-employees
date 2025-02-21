package org.example.util;

public class LoggingConstants {

    public static final String CSV_PROCESSING_ERROR = "CSV Processing Error: {}";
    public static final String IO_ERROR = "IO Error: Failed to read file";
    public static final String PROCESSING_TIME = "Finding longest collaboration took: {} ms";
    public static final String LONGEST_COLLABORATION = "Longest collaboration: Employees {} and {} worked together for {} days";
    public static final String NO_COLLABORATIONS = "No collaborations found";
    public static final String INVALID_COLUMNS = "Line {}: Invalid number of columns, expected {} but got {}";
    public static final String CSV_READ_ERROR = "Failed to read CSV file: {}";

    // CSV validation
    public static final String COMMA_DELIMITER = ",";
    public static final int EXPECTED_COLUMN_COUNT = 4;

    // Error messages
    public static final String CSV_EMPTY = "CSV file is empty";
    public static final String INVALID_CSV_FORMAT = "Invalid CSV format: File must contain comma-separated values";
    public static final String INVALID_HEADER = "Invalid header: Expected at least %d columns";
    public static final String NO_VALID_RECORDS = "No valid records found in CSV file";

    // Logging messages
    public static final String INVALID_EMPLOYEE_ID = "Line {}: Invalid employee ID format: {}";
    public static final String INVALID_PROJECT_ID = "Line {}: Invalid project ID format: {}";
    public static final String INVALID_DATE_FROM = "Line {}: Invalid dateFrom format: {}";
    public static final String INVALID_DATE_TO = "Line {}: Invalid dateTo format: {}";
    public static final String INVALID_DATE_RANGE = "Line {}: dateFrom ({}) is after dateTo ({})";
    public static final String NUMBER_PARSING_ERROR = "Line {}: Number parsing error: {}";
    public static final String DATE_PARSING_ERROR = "Line {}: {}";
    public static final String PROJECT_OVERLAP = "Project {}: emp{} & emp{} overlap: {} days";
}