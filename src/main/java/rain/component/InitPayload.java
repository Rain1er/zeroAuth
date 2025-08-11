package rain.component;

import org.apache.commons.lang3.StringUtils;
import rain.component.BaseRequestEntry;
import rain.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitPayload {
    public InitPayload(){};
    public List<BaseRequestEntry> make_payload_v2(String RequestURI,String query) {
        RequestURI = RequestURI.substring(1);

        Boolean endWithSlash = false;

        //以斜杠结尾的URL，如/admin/getUserById/,需要对末尾斜杠增加一轮FUZZ
        if(RequestURI.endsWith("/")) {
            RequestURI = RequestURI.substring(0, RequestURI.length()-1);
            endWithSlash = true;
        }   
        //取得每一级目录
        String[] paths = RequestURI.split("/");

        List<BaseRequestEntry> allRequests = new ArrayList();

        //1.末尾目录添加后缀
        Object suffix = Utils.configMap.get("suffix");
        if(endWithSlash) {
            allRequests.addAll(makeRequestSuffix((List<String>) suffix, RequestURI));
            allRequests.addAll(makeRequestSuffix((List<String>) suffix, RequestURI + "/")); //增加对/后的FUZZ，有点不明白这里
        } else {
            allRequests.addAll(makeRequestSuffix((List<String>) suffix, RequestURI));
        }


        //2.每一级目录添加前缀
        Object prefix = Utils.configMap.get("prefix");
        if( paths.length > 1) {
            int paths_len = paths.length;
            allRequests.addAll(makeRequestPrefix((List<String>) prefix, paths, paths_len));
        }

        //2.1.每一级目录添加后缀，排除末尾目录
        Object middleSuffix = Utils.configMap.get("middle_suffix");
        if( paths.length > 1) {
            int paths_len = paths.length;
            allRequests.addAll(makeRequestMidPrefix((List<String>) middleSuffix, paths, paths_len));
        }


        //3.添加认证header头
        Object headersList = Utils.configMap.get("headers");
        allRequests.addAll(makeRequestHeader((List<?>) headersList, RequestURI));

        //最后把查询参数加上
        //取得所有List<BaseRequestEntry> allRequests中BaseRequestEntry对象的path属性，并在其后面加上 "?"和query变量值覆盖原值
        if(!query.isEmpty()){
            for (BaseRequestEntry baseRequestEntry : allRequests) { //对象引用行为，类似于cpp，可直接修改列表
                baseRequestEntry.path = baseRequestEntry.path + "?" + query;
            }
        }


        return allRequests;
    }


    public static List<BaseRequestEntry> makeRequestSuffix(List<String> suffixList, String RequestURI){

        List<BaseRequestEntry> baseRequestList = new ArrayList();

        for (Object item : suffixList) {
            //System.out.println( "/" + RequestURI + item);
            baseRequestList.add(new BaseRequestEntry("GET",  "/" + RequestURI + item, null));
        }

        return baseRequestList;
    }

    //给目录前端添加FUZZ
    public static List<BaseRequestEntry> makeRequestPrefix(List<String> prefixList, String[] paths, int paths_len){

        List<BaseRequestEntry> baseRequestList = new ArrayList();

        for (Object item : prefixList) {  //拿到所有payload
            for (int i=0; i < paths_len; i++) { //分别插入到每级目录前
                String _target = paths[i];
                paths[i] = item + _target;
                String newRequestURI = StringUtils.join(paths, "/");
                //System.out.println("/" + newRequestURI);
                baseRequestList.add(new BaseRequestEntry("GET",   "/" + newRequestURI, null));

                paths[i] = _target;
            }
        }

        return baseRequestList;
    }

    //new:相对与BypassPro，增加对非末尾目录对空字符的fuzz
    public static List<BaseRequestEntry> makeRequestMidPrefix(List<String> midSuffixList, String[] paths, int paths_len){

        List<BaseRequestEntry> baseRequestList = new ArrayList();

        for (Object item : midSuffixList) {  //拿到所有payload
            for (int i=0; i < paths_len-1; i++) { //分别插入到每级目录后,排除末尾目录
                String _target = paths[i];
                paths[i] =  _target + item;
                String newRequestURI = StringUtils.join(paths, "/") ; //path合并成uri
                //System.out.println("/" + newRequestURI);
                baseRequestList.add(new BaseRequestEntry("GET",   "/" + newRequestURI, null));

                paths[i] = _target;
            }
        }

        return baseRequestList;
    }

    public static List<BaseRequestEntry> makeRequestHeader(List<?> headerList, String RequestURI){
        List<BaseRequestEntry> baseRequestList = new ArrayList();
        for (Object item : headerList) {

            if(item instanceof Map){
                baseRequestList.add(new BaseRequestEntry("GET",   "/" + RequestURI, (Map<String, String>) ((HashMap<String, String>) item).clone()));
            }

        }
        return baseRequestList;
    }
}
