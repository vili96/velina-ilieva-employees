package org.example.ui;

import org.example.core.Main;
import org.example.model.EmployeePair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.SortedMap;

import static org.example.util.ErrorConstants.ERROR_DIALOG_TITLE;
import static org.example.util.ErrorConstants.ERROR_MESSAGE_PREFIX;
import static org.example.util.UIConstants.*;

public class EmployeeCollaborationUI extends JFrame {
    private DefaultTableModel tableModel;
    private final JLabel congratsLabel;

    private record MaxCollaboration(int emp1, int emp2, int projectId, long days) {
    }

    public EmployeeCollaborationUI() {
        setupMainFrame();
        var mainPanel = createMainPanel();
        setContentPane(mainPanel);

        var topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        congratsLabel = createCongratsLabel();
        mainPanel.add(congratsLabel, BorderLayout.CENTER);

        var scrollPane = createTableScrollPane();
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        setupSystemLookAndFeel();
    }

    private void setupMainFrame() {
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_SIZE);
        setLocationRelativeTo(null);
        setMinimumSize(MIN_WINDOW_SIZE);
    }

    private JPanel createMainPanel() {
        var mainPanel = new JPanel(new BorderLayout(COMPONENT_SPACING, COMPONENT_SPACING));
        mainPanel.setBorder(new EmptyBorder(BORDER_PADDING, BORDER_PADDING,
                BORDER_PADDING, BORDER_PADDING));
        return mainPanel;
    }

    private JPanel createTopPanel() {
        var topPanel = new JPanel(new BorderLayout(COMPONENT_SPACING, COMPONENT_SPACING));
        var titleLabel = createTitleLabel();
        var buttonPanel = createButtonPanel();

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JLabel createTitleLabel() {
        var titleLabel = new JLabel(TITLE_TEXT);
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, TITLE_FONT_SIZE));
        return titleLabel;
    }

    private JPanel createButtonPanel() {
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        var chooseFileButton = createStyledButton(BUTTON_TEXT);
        buttonPanel.add(chooseFileButton);
        return buttonPanel;
    }

    private JLabel createCongratsLabel() {
        var label = new JLabel(EMPTY_SPACE);
        label.setFont(new Font(FONT_FAMILY, Font.PLAIN, CONGRATS_FONT_SIZE));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(CONGRATS_BG);
        return label;
    }

    private JScrollPane createTableScrollPane() {
        tableModel = createTableModel();
        JTable resultTable = createTable();

        var scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return scrollPane;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createTable() {
        var table = new JTable(tableModel);
        styleTable(table);
        return table;
    }

    private JButton createStyledButton(String text) {
        var button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font(FONT_FAMILY, Font.PLAIN, NORMAL_FONT_SIZE));
        button.setBackground(BUTTON_BACKGROUND);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(
                BUTTON_PADDING,
                BUTTON_HORIZONTAL_PADDING,
                BUTTON_PADDING,
                BUTTON_HORIZONTAL_PADDING
        ));

        setupButtonHoverEffect(button);
        button.addActionListener(e -> selectFile());

        return button;
    }

    private void setupButtonHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_BACKGROUND);
            }
        });
    }

    private void styleTable(JTable table) {
        setupTableBasicStyle(table);
        setupTableHeaderStyle(table);
        setupTableCellRenderer(table);
    }

    private void setupTableBasicStyle(JTable table) {
        table.setFont(new Font(FONT_FAMILY, Font.PLAIN, NORMAL_FONT_SIZE));
        table.setRowHeight(TABLE_ROW_HEIGHT);
        table.setShowGrid(true);
        table.setGridColor(TABLE_GRID);
        table.setSelectionBackground(TABLE_SELECTION_BG);
        table.setSelectionForeground(Color.BLACK);
    }

    private void setupTableHeaderStyle(JTable table) {
        var header = table.getTableHeader();
        header.setFont(new Font(FONT_FAMILY, Font.BOLD, NORMAL_FONT_SIZE));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(Color.BLACK);

        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void setupTableCellRenderer(JTable table) {
        var centerRenderer = createCenterRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private DefaultTableCellRenderer createCenterRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                var c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT_ROW);
                }
                return c;
            }
        };
    }

    private void setupSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processFile(File file) {
        try {
            tableModel.setRowCount(0);

            var workDurations = Main.parseCsvContent(new FileInputStream(file));
            var sortedResults = Main.findLongestCollaboration(workDurations);

            if (!sortedResults.isEmpty()) {
                var maxCollaboration = extractMaxCollaboration(sortedResults.firstEntry());
                updateCongratsLabel(maxCollaboration);
                populateTableData(sortedResults);
            } else {
                congratsLabel.setText(NO_DATA_MESSAGE);
            }

        } catch (Exception e) {
            showErrorDialog(e.getMessage());
        }
    }

    private MaxCollaboration extractMaxCollaboration(Map.Entry<EmployeePair, Map<Integer, Long>> topEntry) {
        var pair = topEntry.getKey();
        var projectDays = topEntry.getValue();

        var maxProjectEntry = projectDays.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();

        return new MaxCollaboration(
                pair.getEmp1(),
                pair.getEmp2(),
                maxProjectEntry.getKey(),
                maxProjectEntry.getValue()
        );
    }

    private void populateTableData(SortedMap<EmployeePair, Map<Integer, Long>> results) {
        results.forEach((pair, projectDays) -> {
            projectDays.forEach((projectId, days) -> {
                tableModel.addRow(new Object[]{
                        pair.getEmp1(),
                        pair.getEmp2(),
                        projectId,
                        days
                });
            });
        });
    }

    private void updateCongratsLabel(MaxCollaboration maxCollaboration) {
        congratsLabel.setText(String.format(CONGRATULATORY_MESSAGE,
                maxCollaboration.emp1(), maxCollaboration.emp2(),
                maxCollaboration.projectId(), maxCollaboration.days()));
    }

    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(this,
                ERROR_MESSAGE_PREFIX + errorMessage,
                ERROR_DIALOG_TITLE,
                JOptionPane.ERROR_MESSAGE);
    }

    private void selectFile() {
        var fileChooser = new JFileChooser();
        var result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            var selectedFile = fileChooser.getSelectedFile();
            processFile(selectedFile);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            var ui = new EmployeeCollaborationUI();
            ui.setVisible(true);
        });
    }
}