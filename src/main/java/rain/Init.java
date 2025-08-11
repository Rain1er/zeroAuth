package rain;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import rain.ui.Dashboard;
import rain.component.Scan;
import rain.utils.API;
import rain.utils.Utils;

import java.util.Map;

public class Init implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("zeroAuth");
        API.init(api);

        banner(api);

        //加载配置文件
        Map<String, Object> config = Utils.loadConfig("/config.yaml");
        Utils.setConfigMap(config);

        //设置burp标签
        Dashboard dashboard = new Dashboard();
        Utils.setPanel(dashboard);
        api.userInterface().registerSuiteTab("zeroAuth", dashboard);

        // 设置右键菜单
        api.userInterface().registerContextMenuItemsProvider(new Scan());

        // 注册请求处理器
        api.proxy().registerRequestHandler(new Scan());
    }


    private void banner(MontoyaApi api){
        api.logging().logToOutput("===================================");
        api.logging().logToOutput(String.format("%s loaded success", "zeroAuth"));
        api.logging().logToOutput(String.format("version: %s", "1.0"));
        api.logging().logToOutput("raindrop");
        api.logging().logToOutput("===================================");

    }

}
