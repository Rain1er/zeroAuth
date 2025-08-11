package rain.ui;

import burp.api.montoya.http.message.HttpRequestResponse;

import java.net.URL;

public class BypassEntry {

    final String timestamp;
    final int length;
    final HttpRequestResponse requestResponse;
    final String url;
    final short status;
    final String mimeType;
    final String method;
    final String title;
    final long id;
    final String tool;

    public BypassEntry(String timestamp, String method, int length, HttpRequestResponse requestResponse, String url, short status, String mimeType, String title, long id, String tool) {
        this.timestamp = timestamp;
        this.method = method;
        this.length = length;
        this.requestResponse = requestResponse;
        this.url = url;
        this.status = status;
        this.mimeType = mimeType;
        this.title = title;
        this.id = id;
        this.tool = tool;
    }
}

