package rain.utils;

import burp.api.montoya.MontoyaApi;

public final class API {
    private static MontoyaApi INSTANCE;
    private API(){};    // 防止被实例化

    public static void init(MontoyaApi api){
        if(INSTANCE == null){
            INSTANCE = api;
        }
    }

    public static MontoyaApi getAPI() {
        return INSTANCE;
    }
}
