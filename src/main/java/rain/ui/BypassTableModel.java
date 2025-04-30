package rain.ui;


import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

//扫描结果的数据结构
public class BypassTableModel extends AbstractTableModel {


    private final List<BypassEntry> bypassEntryArray = new ArrayList();

    public int getRowCount() {

        return bypassEntryArray.size();
    }

    public int getColumnCount() {

        return 9;
    }

    @Override
    public String getColumnName(int columnIndex) {

        switch (columnIndex) {
            case 0:
                return "id";
            case 1:
                return "tool";
            case 2:
                return "Title";
            case 3:
                return "Method";
            case 4:
                return "Length";
            case 5:
                return "Request URL";
            case 6:
                return "MIME Type";
            case 7:
                return "HTTP Status";
            case 8:
                return "Time";
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {

        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            case 4:
                return String.class;
            case 5:
                return String.class;
            case 6:
                return String.class;
            case 7:
                return Short.class;
            case 8:
                return String.class;
            default:
                return Object.class;
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {

        BypassEntry bypassEntry = bypassEntryArray.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return bypassEntry.id;

            case 1:
                return bypassEntry.tool;
            case 2:
                return bypassEntry.title;
            case 3:
                return bypassEntry.method;
            case 4:
                return bypassEntry.length;
            case 5:
                return bypassEntry.url.toString();
            case 6:
                return bypassEntry.mimeType;
            case 7:
                return bypassEntry.status;
            case 8:
                return bypassEntry.timestamp;
            default:
                return "";
        }
    }

    public List<BypassEntry> getBypassArray() {

        return bypassEntryArray;
    }

}
