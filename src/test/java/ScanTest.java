import org.junit.Test;
import rain.component.BaseRequestEntry;
import rain.component.InitPayload;
import rain.utils.Utils;

import java.util.Iterator;
import java.util.List;

public class ScanTest {
    @Test
    public void testPaylod(){
        System.out.println(Utils.loadConfig("/config.yaml"));
        //new Main().make_payload_v2("/admin/getUserById/?id=1");
        List<BaseRequestEntry> baseRequests = new InitPayload().make_payload_v2("/C6/JHSoft.Web.WorkFlat/RssModulesHttp.aspx","interfaceID=1");

        //用迭代器输出allRequests内容
        Iterator<BaseRequestEntry> iterator = baseRequests.iterator();
        while ( iterator.hasNext()) {
            BaseRequestEntry request = iterator.next();
            System.out.println(request.path);
        }
    }
}
