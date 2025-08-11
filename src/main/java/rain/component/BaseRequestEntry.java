package rain.component;

import java.util.Map;

public class BaseRequestEntry {
    public String method;
    public String path;
    public Map<String, String> headers;

    public BaseRequestEntry(String method, String path, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.headers = headers;
    }

    public String toString() {
        return method + " : " + path;
    }
}