package rain.ui;

import burp.api.montoya.http.message.HttpRequestResponse;
import rain.ui.BypassEntry;
import rain.utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    public Log(){};

    public static void addLog(HttpRequestResponse messageInfo, int toolFlag, long time, int row, String title, String mode)  {
        short statusCode = messageInfo.response().statusCode();
        // 405请求方法错误  415 content-type错误
        if(statusCode == 200 || statusCode == 405 || statusCode == 415 ) {
            Utils.panel.getBypassTableModel().getBypassArray().add(new BypassEntry(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()),
                    messageInfo.request().method(),
                    messageInfo.request().toString().length(),
                    messageInfo,
                    messageInfo.request().url(),
                    messageInfo.response().statusCode(),
                    messageInfo.response().mimeType().toString(),
                    title,
                    Utils.count++, mode));
            Utils.panel.getBypassTableModel().fireTableRowsInserted(row, row);
        }
    }

    public static synchronized void addAllRequestNum(int num) {
        Utils.panel.addAllRequestNum(num);
    }
    public static synchronized void addFinishRequestNum(int num) {
        Utils.panel.addFinishRequestNum(num);
    }


}
