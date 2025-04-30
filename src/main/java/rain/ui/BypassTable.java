package rain.ui;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import rain.utils.API;

import javax.swing.*;

public class BypassTable extends JTable {
    public BypassTableModel bypassTableModel;
    public HttpRequestEditor requestViewer;
    public HttpResponseEditor responseViewer;
    public HttpRequestResponse currentlyDisplayedItem;

    //请求条目表格，接受来自BypassTableModel的数据并设置格式如宽度，给JScrollPane进行渲染
    //BypassTable是BypassTableModel和JScrollPane的中间接口
    //定义和管理表格数据。
    public BypassTable(BypassTableModel bypassTableModel) {
        super(bypassTableModel);
        this.bypassTableModel = bypassTableModel;
        this.requestViewer = API.getAPI().userInterface().createHttpRequestEditor(EditorOptions.READ_ONLY);
        this.responseViewer = API.getAPI().userInterface().createHttpResponseEditor(EditorOptions.READ_ONLY);

        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        getColumnModel().getColumn(0).setMinWidth(80);
        getColumnModel().getColumn(1).setMinWidth(100);
        getColumnModel().getColumn(2).setMinWidth(150);
        getColumnModel().getColumn(3).setMinWidth(100);
        getColumnModel().getColumn(4).setMinWidth(100);
        getColumnModel().getColumn(5).setPreferredWidth(1100);
        getColumnModel().getColumn(6).setMinWidth(100);
        getColumnModel().getColumn(7).setMinWidth(80);
        getColumnModel().getColumn(8).setMinWidth(120);
        setAutoCreateRowSorter(true);
    }

    //查看日志记录
    @Override
    public void changeSelection(int row, int col, boolean toggle, boolean extend) {
        BypassEntry bypassEntry = bypassTableModel.getBypassArray().get(convertRowIndexToModel(row));
        requestViewer.setRequest(bypassEntry.requestResponse.request());
        responseViewer.setResponse(bypassEntry.requestResponse.response());
        currentlyDisplayedItem = bypassEntry.requestResponse;

        super.changeSelection(row, col, toggle, extend);
    }

}
