package rain.component;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import rain.utils.API;
import rain.ui.Log;
import rain.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static rain.utils.Utils.compare;



public class Scan implements ContextMenuItemsProvider, ProxyRequestHandler {

    //主动扫描
    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<Component> list = new ArrayList<>();

        JMenuItem jMenuItem = new JMenuItem("Send to zeroAuth");
        //绑定点击事件，进行主动扫描。
        jMenuItem.addActionListener(e -> new Thread(() -> {
            //这里有bug，只能从proxy里面拿条目进行扫描
            List<HttpRequestResponse> httpRequestResponses = new ArrayList<>();
            //修复：从编辑框框发送、或从列表条目发送
            if(!event.messageEditorRequestResponse().isEmpty()){
                httpRequestResponses.add(event.messageEditorRequestResponse().get().requestResponse());
            }else {
                httpRequestResponses.addAll(event.selectedRequestResponses());
            }
            //List<HttpRequestResponse> httpRequestResponses = event.selectedRequestResponses();
            processHttp(httpRequestResponses, "active"); //click，主动扫描
        }).start());
        list.add(jMenuItem);

        return list;
    }

    //被动扫描
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        HttpRequestResponse httpRequestResponse = API.getAPI().http().sendRequest(interceptedRequest);
        if (Utils.isProxySelected && Run_request.isScan(httpRequestResponse)) {

            //List<HttpRequestResponse> httpRequestResponses = null;  //为了扫描格式统一性，这里空指针异常！！
            List<HttpRequestResponse> httpRequestResponses = new ArrayList<>();  // 初始化列表
            httpRequestResponses.add(httpRequestResponse);

            short old_status = httpRequestResponse.response().statusCode();
            if ((old_status == 302 || old_status == 401 || old_status == 403 || old_status == 404)) {
                new Thread(() -> {
                    processHttp(httpRequestResponses, "passive");
                }).start();
            }
        }
        return null;
    }


    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return null;
    }


    //扫描准备工作
    //0.获取用户发送的HttpRequestResponse对象
    //1.计数
    //2.提交扫描线程（主动or被动）
    public void processHttp(List<HttpRequestResponse> httpRequestResponses, String mode) {
        for (HttpRequestResponse httpRequestResponse : httpRequestResponses) {
            //是否有必要进行扫描？
            if(Run_request.isScan(httpRequestResponse)){
                //记录原始请求相关信息
                String old_path = httpRequestResponse.request().pathWithoutQuery(); //这里有问题，bypasspro是通过拼接url解决
                String query = httpRequestResponse.request().query();
                String old_method = httpRequestResponse.request().method();
                String old_resp = httpRequestResponse.response().toString();

                //生成paylaod
                List<BaseRequestEntry> payloads = new InitPayload().make_payload_v2(old_path,query);
                Log.addAllRequestNum(payloads.size()*2);        //计算请求数量,因为要FUZZ不同方法，所以乘2


                int thread_num = Utils.panel.getThreadNum();    //获取用户自定义线程
                System.out.println(("start thread, number: " + String.valueOf(thread_num) + " path: " + old_path));

                //提交任务
                ExecutorService es = Executors.newFixedThreadPool(thread_num);
                for (BaseRequestEntry payload : payloads) {
                    es.submit(new Run_request(payload, old_path, old_method, old_resp, httpRequestResponse, mode));
                }
                es.shutdown();
            }

        }
    }



    //扫描执行类
    private class Run_request implements Runnable {
        public MontoyaApi api;
        public BaseRequestEntry baseRequest;
        public String old_path;
        public String old_resp;
        public String old_method;
        public HttpRequestResponse httpRequestResponse;
        public String mode;


        //构造函数
        public Run_request(BaseRequestEntry baseRequest, String old_path, String old_method, String old_resp, HttpRequestResponse httpRequestResponse, String mode) {
            this.api = API.getAPI();
            this.baseRequest = baseRequest;
            this.old_path = old_path;
            this.old_method = old_method;
            this.old_resp = old_resp;
            this.httpRequestResponse = httpRequestResponse;
            this.mode = mode;
        }

        @Override
        public void run() {
            //开扫
            //FUZZ请求方法,来自浏览器的只有get和post
            // 如果是get请求，分别fuzz post和trace。如果是post请求，fuzz get和post
            HttpRequestResponse new_httpRequestResponse;
            if (httpRequestResponse.request().method().equals("GET")) {
                new_httpRequestResponse = Utils.sentRequest(httpRequestResponse, "POST", baseRequest.path);
                compare(httpRequestResponse,new_httpRequestResponse,mode);

                new_httpRequestResponse = Utils.sentRequest(httpRequestResponse, "TRACE", baseRequest.path);
                compare(httpRequestResponse,new_httpRequestResponse,mode);

            }else{
                new_httpRequestResponse = Utils.sentRequest(httpRequestResponse, "POST", baseRequest.path);
                compare(httpRequestResponse,new_httpRequestResponse,mode);

                new_httpRequestResponse = Utils.sentRequest(httpRequestResponse, "TRACE", baseRequest.path);
                compare(httpRequestResponse,new_httpRequestResponse,mode);
            }
        }

        public static boolean isScan(HttpRequestResponse httpRequestResponse){
            String path = httpRequestResponse.request().path().toLowerCase();       //注意这里path可以获得query参数
            if (path.lastIndexOf(".") > -1) {  // 文件是这些静态后缀，不检测
                String extension = path.substring(path.lastIndexOf(".") + 1);
                ArrayList<String> excludeExtensions = new ArrayList<>(Arrays.asList(    //from HAE
                        "3g2", "3gp", "7z", "aac", "abw", "aif", "aifc", "aiff", "apk", "arc", "au", "avi", "azw",
                        "bat", "bin", "bmp", "bz", "bz2", "cmd", "cmx", "cod", "com", "csh", "css", "csv", "dll",
                        "doc", "docx", "ear", "eot", "epub", "exe", "flac", "flv", "gif", "gz", "ico", "ics", "ief",
                        "jar", "jfif", "jpe", "jpeg", "jpg", "less", "m3u", "mid", "midi", "mjs", "mkv", "mov",
                        "mp2", "mp3", "mp4", "mpa", "mpe", "mpeg", "mpg", "mpkg", "mpp", "mpv2", "odp", "ods", "odt",
                        "oga", "ogg", "ogv", "ogx", "otf", "pbm", "pdf", "pgm", "png", "pnm", "ppm", "ppt", "pptx",
                        "ra", "ram", "rar", "ras", "rgb", "rmi", "rtf", "scss", "sh", "snd", "svg", "swf", "tar",
                        "tif", "tiff", "ttf", "vsd", "war", "wav", "weba", "webm", "webp", "wmv", "woff", "woff2",
                        "xbm", "xls", "xlsx", "xpm", "xul", "xwd", "zip","js","map","so","iso"
                ));
                if(excludeExtensions.contains(extension))
                    return false;
            }
            return true;
        }

    }

}
