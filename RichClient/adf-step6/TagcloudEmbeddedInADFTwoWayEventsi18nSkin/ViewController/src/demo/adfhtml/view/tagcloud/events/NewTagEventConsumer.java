package demo.adfhtml.view.tagcloud.events;


import demo.adfhtml.view.ADFHelper;
import demo.adfhtml.view.Tag;
import demo.adfhtml.view.tagcloud.TagCloudBean;

import java.util.Map;


public class NewTagEventConsumer {
    public void handleEvent(Object payload) {
        TagCloudBean tcb =
            (TagCloudBean)ADFHelper.evaluateEL("#{pageFlowScope.tagCloudBean}");
      //  tcb.handleNewTagEvent(new Tag("newtag received " + payload, 43), "aa");
        Tag tag = (Tag)((Map)payload).get("tag");
        String clientId = (String)((Map)payload).get("clientId");
        tcb.handleNewTagEvent(tag, clientId);
    }
}
