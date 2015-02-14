package demo.adfhtml.view.events;

import demo.adfhtml.view.ADFHelper;
import demo.adfhtml.view.MatchHelperBean;

import demo.adfhtml.view.Tag;
import demo.adfhtml.view.TagCloudBean;

import java.util.List;

import oracle.adf.share.logging.ADFLogger;

public class AvailableTagsCollectionChangedEventConsumer {
    ADFLogger _logger = ADFLogger.createADFLogger(this.getClass());
    public void handleEvent(Object payload) {
        _logger.warning("Event consumed - payload =  "+payload);
     TagCloudBean tcb = (TagCloudBean)ADFHelper.evaluateEL("#{pageFlowScope.tagCloudBean}");
     List<Tag> tagCloud = (List<Tag>)payload;
        _logger.warning(" Tag Cloud Bean to be invoked "+tcb);
     tcb.setTagsFromEvent(tagCloud);
    }
}
