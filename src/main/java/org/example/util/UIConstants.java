package org.example.util;

import java.awt.*;

public class UIConstants {

    public static final String WINDOW_TITLE = "Employee Collaboration Analyzer";
    public static final Dimension WINDOW_SIZE = new Dimension(1000, 600);
    public static final Dimension MIN_WINDOW_SIZE = new Dimension(600, 400);
    public static final String NO_DATA_MESSAGE = "No collaboration data found";

    // Labels and text
    public static final String EMPTY_SPACE = " ";
    public static final String TITLE_TEXT = "Employee Collaboration Analysis";
    public static final String BUTTON_TEXT = "Choose CSV File";
    // Table columns
    public static final String[] TABLE_COLUMNS = {
            "Employee ID #1",
            "Employee ID #2",
            "Project ID",
            "Days worked"
    };

    // Font properties
    public static final String FONT_FAMILY = "Arial";
    public static final int TITLE_FONT_SIZE = 18;
    public static final int NORMAL_FONT_SIZE = 14;
    public static final int CONGRATS_FONT_SIZE = 16;

    // Colors
    public static final Color BUTTON_BACKGROUND = new Color(64, 64, 64);
    public static final Color BUTTON_HOVER = new Color(96, 96, 96);
    public static final Color TABLE_HEADER_BG = new Color(240, 240, 240);
    public static final Color TABLE_GRID = new Color(230, 230, 230);
    public static final Color TABLE_SELECTION_BG = new Color(232, 242, 254);
    public static final Color TABLE_ALT_ROW = new Color(248, 248, 248);
    public static final Color CONGRATS_BG = new Color(235, 245, 255);

    // Sizes and spacing
    public static final int BORDER_PADDING = 15;
    public static final int COMPONENT_SPACING = 10;
    public static final int TABLE_ROW_HEIGHT = 30;
    public static final int BUTTON_PADDING = 8;
    public static final int BUTTON_HORIZONTAL_PADDING = 15;
    public static final String CONGRATULATORY_MESSAGE =
            "<html>Congratulations to the most devoted team players!<br/>Employees %d and %d have worked on project %d for %d days!</html>";

}
