package org.chartsy.updatecenter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Viorel
 */
public class DownloadTableModel extends AbstractTableModel
        implements Observer
{

    private static final String[] columnNames =
    {"File Name", "Size", "Progress", "Status"};

    private static final Class[] columnClasses =
    {String.class, String.class, JProgressBar.class, String.class};

    private ArrayList downloadList = new ArrayList();

    public void addDownload(Download download)
    {
        download.addObserver(this);
        downloadList.add(download);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public Download getDownload(int row)
    {
        return (Download) downloadList.get(row);
    }

    public void clearDownload(int row)
    {
        downloadList.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public int getColumnCount()
    {
        return columnNames.length;
    }

    public String getColumnName(int col)
    {
        return columnNames[col];
    }

    public Class getColumnClass(int col)
    {
        return columnClasses[col];
    }

    public int getRowCount()
    {
        return downloadList.size();
    }

    public Object getValueAt(int row, int col)
    {
        Download download = (Download) downloadList.get(row);
        switch (col)
        {
            case 0:
                return download.getFileName(download.getUrl());
            case 1:
                DecimalFormat df = new DecimalFormat("#,##0.00");
                int size = download.getSize();
                return (size == -1)
                        ? "Unknown size"
                        : df.format((double)size/(1024*1024)) + " MB";
            case 2:
                return new Float(download.getProgress());
            case 3:
                return Download.STATUSES[download.getStatus()];
        }
        return "";
    }

    public void update(Observable o, Object arg)
    {
        int index = downloadList.indexOf(o);
        fireTableRowsUpdated(index, index);
    }

}
