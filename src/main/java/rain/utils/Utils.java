package rain.utils;


import burp.api.montoya.http.message.HttpRequestResponse;
import org.yaml.snakeyaml.Yaml;
import rain.ui.Dashboard;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static rain.ui.Log.addFinishRequestNum;
import static rain.ui.Log.addLog;

public class Utils {
    public static Dashboard panel;
    public static long count = 0;
    //主动扫描 默认为faulse
    public static boolean isProxySelected = false;
    public static Map<String, Object> configMap = null;
    public static HttpRequestResponse requestResponse;

    public static void setPanel(Dashboard inpanel) {
        panel = inpanel;
    }

    public static boolean setConfigMap(Map<String, Object> config) {

        if (config.isEmpty()){
            System.out.println("!! config内容为空,将保持原来的payload");
            return false;
        }
        Utils.configMap = config;
        return true;
    }

    public static String getBodyTitle(String s) {
        String regex;
        String title = "";
        final List<String> list = new ArrayList<String>();
        regex = "<title>.*?</title>";
        final Pattern pa = Pattern.compile(regex, Pattern.CANON_EQ);
        final Matcher ma = pa.matcher(s);
        while (ma.find()) {
            list.add(ma.group());
        }

        for (int i = 0; i < list.size(); i++) {
            title = title + list.get(i);
        }

        return title.replaceAll("<.*?>", "");
    }
    //加载配置文件
    public static Map<String, Object> loadConfig(String filename){
        Map<String, Object> yamlMap=null;
        // 读取YAML文件
        try {
            InputStream inputStream = Utils.class.getResourceAsStream(filename);

            Yaml yaml = new Yaml();
            // 将YAML文件的内容加载为Map对象
            yamlMap = yaml.load(inputStream);
            inputStream.close();
        } catch (Exception exception) {
            System.out.println("配置文件加载失败，请检查配置文件");
            exception.printStackTrace();
        }
        configMap = yamlMap;
        return yamlMap;
    }
    public static HttpRequestResponse sentRequest(HttpRequestResponse httpRequestResponse, String method, String path){
        try {
            requestResponse = API.getAPI().http().sendRequest(httpRequestResponse.request().withMethod(method).withPath(path));
            addFinishRequestNum(1);
        } catch (Throwable ee) {
            Utils.panel.addErrorRequestNum(1);
        }
        return requestResponse;
    }

    public static  void compare(HttpRequestResponse oldMessage, HttpRequestResponse newMessage,String mode){
        //优化，排除405（请求方法不允许）状态码
        if(newMessage.response().statusCode() == 405){
            return;
        }

        String old_response = new String(oldMessage.response().toByteArray().getBytes(), StandardCharsets.UTF_8);
        String new_response = new String(newMessage.response().toByteArray().getBytes(), StandardCharsets.UTF_8);
        if (new_response != null && (DiffPage.getRatio(old_response, new_response) <=1 ))   //差异较大
        {
            String title = Utils.getBodyTitle(new_response);
            addLog(newMessage, 0, 0, 0, title, mode);
        }
    }
}
