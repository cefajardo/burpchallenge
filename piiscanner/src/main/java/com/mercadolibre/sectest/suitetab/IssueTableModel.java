package com.mercadolibre.sectest.suitetab;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class IssueTableModel extends AbstractTableModel {
    
    private final List<Issue> log;

    public IssueTableModel() {
        this.log = new ArrayList<>();
    }

    @Override
    public synchronized int getRowCount() {
        return log.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {
        return switch (column) {
            case 0 -> "Date";
            case 1 -> "Issue type";
            case 2 -> "URL";
            case 3 -> "Body";
            case 4 -> "Message"; 
            default -> "";
        };
    }

    @Override
    public synchronized Object getValueAt(int rowIndex, int columnIndex) {
        Issue issue = log.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> issue.getDate();
            case 1 -> issue.getType(); 
            case 2 -> issue.getUrl(); 
            case 3 -> issue.getBody();
            case 4 -> issue.getMessage();   
            default -> "";
        };
    }

    public synchronized void add(Issue issue) {
        int index = log.size();
        log.add(issue);
        fireTableRowsInserted(index, index);
    }

    public synchronized Issue get(int rowIndex) {
        return log.get(rowIndex);
    }
}
