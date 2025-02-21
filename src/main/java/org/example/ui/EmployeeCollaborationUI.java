package org.example.ui;

import org.example.core.Main;
import org.example.model.EmployeePair;
import org.example.model.EmployeeWorkDuration;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import static org.example.util.UIConstants.*;

public class EmployeeCollaborationUI extends JFrame {
    private final JTable resultTable;
    private final DefaultTableModel tableModel;
    private final JLabel congratsLabel;

    public EmployeeCollaborationUI() {
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_SIZE);
        setLocationRelativeTo(null);
        setMinimumSize(MIN_WINDOW_SIZE);

        var mainPanel = new JPanel(new BorderLayout(COMPONENT_SPACING, COMPONENT_SPACING));
        mainPanel.setBorder(new EmptyBorder(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        setContentPane(mainPanel);

        // Top panel for button and title
        var topPanel = new JPanel(new BorderLayout(COMPONENT_SPACING, COMPONENT_SPACING));

        var titleLabel = new JLabel(TITLE_TEXT);
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, TITLE_FONT_SIZE));
        topPanel.add(titleLabel, BorderLayout.WEST);

        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        var chooseFileButton = createStyledButton(BUTTON_TEXT);
        buttonPanel.add(chooseFileButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Congratulatory label setup
        congratsLabel = new JLabel(" ");
        congratsLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, CONGRATS_FONT_SIZE));
        congratsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        congratsLabel.setOpaque(true);
        congratsLabel.setBackground(CONGRATS_BG);
        mainPanel.add(congratsLabel, BorderLayout.CENTER);

        // Table setup
        tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultTable = new JTable(tableModel);
        styleTable(resultTable);

        var scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        chooseFileButton.addActionListener(e -> selectFile());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_BACKGROUND);
            }
        });

        return button;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font(FONT_FAMILY, Font.PLAIN, NORMAL_FONT_SIZE));
        table.getTableHeader().setFont(new Font(FONT_FAMILY, Font.BOLD, NORMAL_FONT_SIZE));
        table.setRowHeight(TABLE_ROW_HEIGHT);
        table.getTableHeader().setBackground(TABLE_HEADER_BG);
        table.getTableHeader().setForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setGridColor(TABLE_GRID);
        table.setSelectionBackground(TABLE_SELECTION_BG);
        table.setSelectionForeground(Color.BLACK);

        // Center everything using a single renderer
        var centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                var c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT_ROW);
                }
                return c;
            }
        };

        // Apply the center renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Center the headers
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void processFile(File file) {
        try {
            tableModel.setRowCount(0);

            Set<EmployeeWorkDuration> workDurations = Main.parseCsvContent(new FileInputStream(file));
            Map<EmployeePair, Map<Integer, Long>> results = Main.findLongestCollaboration(workDurations);

            // Find the pair with maximum collaboration time
            long maxDays = 0;
            int emp1 = 0, emp2 = 0, projectId = 0;

            for (Map.Entry<EmployeePair, Map<Integer, Long>> entry : results.entrySet()) {
                for (Map.Entry<Integer, Long> projectEntry : entry.getValue().entrySet()) {
                    if (projectEntry.getValue() > maxDays) {
                        maxDays = projectEntry.getValue();
                        emp1 = entry.getKey().getEmp1();
                        emp2 = entry.getKey().getEmp2();
                        projectId = projectEntry.getKey();
                    }
                }
            }

            congratsLabel.setText(String.format(CONGRATULATORY_MESSAGE, emp1, emp2, projectId, maxDays));

            populateTableData(results);
            setupTableSorting();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error processing file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processFile(selectedFile);
        }
    }

    private void populateTableData(Map<EmployeePair, Map<Integer, Long>> results) {
        results.forEach((pair, projectDays) -> {
            projectDays.forEach((projectId, days) -> {
                tableModel.addRow(new Object[]{
                        pair.getEmp1(),
                        pair.getEmp2(),
                        projectId,
                        Long.valueOf(days)
                });
            });
        });
    }

    private void setupTableSorting() {
        TableRowSorter<DefaultTableModel> sorter = createCustomSorter();
        setColumnComparators(sorter);
        applySorting(sorter);
    }

    private TableRowSorter<DefaultTableModel> createCustomSorter() {
        return new TableRowSorter<DefaultTableModel>(tableModel) {
            @Override
            public void toggleSortOrder(int column) {
                if (this.getSortKeys().isEmpty()) {
                    this.setSortKeys(Arrays.asList(new RowSorter.SortKey(column, SortOrder.DESCENDING)));
                } else {
                    super.toggleSortOrder(column);
                }
            }
        };
    }

    private void setColumnComparators(TableRowSorter<DefaultTableModel> sorter) {
        // Create comparators that handle Object types properly
        Comparator<Object> intComparator = (o1, o2) -> {
            if (o1 instanceof Number n1 && o2 instanceof Number n2) {
                return Integer.compare(n1.intValue(), n2.intValue());
            }
            return 0;
        };

        Comparator<Object> longComparator = (o1, o2) -> {
            if (o1 instanceof Number n1 && o2 instanceof Number n2) {
                return Long.compare(n1.longValue(), n2.longValue());
            }
            return 0;
        };

        sorter.setComparator(0, intComparator);  // Employee ID #1
        sorter.setComparator(1, intComparator);  // Employee ID #2
        sorter.setComparator(2, intComparator);  // Project ID
        sorter.setComparator(3, longComparator); // Days worked
    }

    private void applySorting(TableRowSorter<DefaultTableModel> sorter) {
        resultTable.setRowSorter(sorter);
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(3, SortOrder.DESCENDING)));
        sorter.sort();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeeCollaborationUI ui = new EmployeeCollaborationUI();
            ui.setVisible(true);
        });
    }
}