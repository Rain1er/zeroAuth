package rain.ui;

import org.apache.commons.lang3.StringUtils;
import rain.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;


public class Dashboard extends JPanel  {
    private BypassTableModel bypassTableModel;
    private JTextField threadNumText;
    private JTextField allRequestNumberText;
    private JTextField finishRequestNumberText;
    private JTextField errorRequestNumText;
    private JCheckBox isAutoCheckBox;
    public Dashboard() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // main split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        //1.table of log entries
        bypassTableModel = new BypassTableModel();
        BypassTable bypassTable = new BypassTable(bypassTableModel);
        JScrollPane scrollPane = new JScrollPane(bypassTable);
//        splitPane.setLeftComponent(scrollPane);
        splitPane.setTopComponent(scrollPane);

        //2.request response 双窗格显示
        JSplitPane httpSplitPane = new JSplitPane();
        httpSplitPane.setResizeWeight(0.50);
        // request
        JTabbedPane reqJTabbedPane = new JTabbedPane();
        reqJTabbedPane.add("Request",bypassTable.requestViewer.uiComponent());

        // response
        JTabbedPane resJTabbedPane = new JTabbedPane();
        resJTabbedPane.add("Response", bypassTable.responseViewer.uiComponent());

        httpSplitPane.add(reqJTabbedPane,"left");
        httpSplitPane.add(resJTabbedPane,"right");
//        splitPane.setRightComponent(httpSplitPane);
        splitPane.setBottomComponent(httpSplitPane);


        //3.配置框 controlPanel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        isAutoCheckBox = new JCheckBox("Passive Scan", false);
        controlPanel.add(isAutoCheckBox);
        //是否开启被动扫描
        ActionListener actionListener = e -> {
            if(isAutoCheckBox.isSelected())
                Utils.isProxySelected = true;
        };
        isAutoCheckBox.addActionListener(actionListener);


        JLabel threadNumLabel = new JLabel("Thread Num:");
        controlPanel.add(threadNumLabel);

        threadNumText = new JTextField(10);
        threadNumText.setText("10");
        controlPanel.add(threadNumText);


        JLabel allRequestNumberLabel = new JLabel("AllRequest Num:");
        controlPanel.add(allRequestNumberLabel);
        allRequestNumberText = new JTextField(5);   //最多可显示5位
        allRequestNumberText.setText("0");
        allRequestNumberText.setEditable(false);
        controlPanel.add(allRequestNumberText);

        JLabel finishNumberLabel = new JLabel("Finish Num:");
        controlPanel.add(finishNumberLabel);
        finishRequestNumberText = new JTextField(5);
        finishRequestNumberText.setText("0");
        finishRequestNumberText.setEditable(false);
        controlPanel.add(finishRequestNumberText);


        JLabel errorRequestNumLabel = new JLabel("Error Num:");
        controlPanel.add(errorRequestNumLabel);
        errorRequestNumText = new JTextField(5);
        errorRequestNumText.setText("0");
        errorRequestNumText.setEditable(false);
        controlPanel.add(errorRequestNumText);


        JButton clearButton = new JButton("Clear");

        controlPanel.add(clearButton);

        clearButton.addActionListener(e -> {
            bypassTableModel.getBypassArray().clear();
            bypassTableModel.fireTableDataChanged();
            allRequestNumberText.setText("0");
            finishRequestNumberText.setText("0");
            errorRequestNumText.setText("0");
            Utils.count = 0;
        });

        controlPanel.setAlignmentX(0);

        // 添加重载配置文件控件
        JButton reconfigButton = new JButton("reconfig");
        controlPanel.add(reconfigButton);
        reconfigButton.addActionListener(e -> {
            //todo，支持自定义配置文件导入
            Map<String, Object> config = Utils.loadConfig("/config.yaml");
            Utils.setConfigMap(config);
            System.out.println("reconfig success...");
        });


        // todo:添加filter


        add(controlPanel);
        add(splitPane);
    }


    public int getThreadNum() {

        if(StringUtils.isBlank(threadNumText.getText())) {
            return 10;   //默认10线程
        }

        return Integer.parseInt(threadNumText.getText());
    }

    public BypassTableModel getBypassTableModel() {

        return bypassTableModel;
    }

    public void setAllRequestNumberText(int num) {
        allRequestNumberText.setText(String.valueOf(num));
    }

    public void addAllRequestNum(int num) {
        setAllRequestNumberText(Integer.parseInt(allRequestNumberText.getText()) + num);
    }

    public void addFinishRequestNum(int num) {
        finishRequestNumberText.setText(String.valueOf(Integer.parseInt(finishRequestNumberText.getText()) + num));
    }

    public void addErrorRequestNum(int num) {
        errorRequestNumText.setText(String.valueOf(Integer.parseInt(errorRequestNumText.getText()) + num));
    }


    public JCheckBox getIsAutoCheckBox() {
        return isAutoCheckBox;
    }

    public void setIsAutoCheckBox(JCheckBox isAutoCheckBox) {
        this.isAutoCheckBox = isAutoCheckBox;
    }

}
