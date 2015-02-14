package demo.adfhtml.view.events;

import demo.adfhtml.view.ADFHelper;
import demo.adfhtml.view.MatchDetailsHelperBean;
import demo.adfhtml.view.MatchHelperBean;
import demo.adfhtml.view.Tag;
import demo.adfhtml.view.TagCloudBean;

import java.util.List;

import oracle.adf.share.logging.ADFLogger;

public class TagAddedEventConsumer {
    public TagAddedEventConsumer() {
        super();
    }
    
    ADFLogger _logger = ADFLogger.createADFLogger(this.getClass());
 
    public void handleEvent(Object payload) {
        _logger.warning("tag added Event consumed - payload =  "+payload);

        _logger.warning("Event consumed - payload =  "+payload);
        MatchDetailsHelperBean mdhb = (MatchDetailsHelperBean)ADFHelper.evaluateEL("#{pageFlowScope.matchDetailsHelperBean}");
        String newTag = (String)payload;
        _logger.warning(" Match Helper Bean to be invoked "+mdhb);
        mdhb.addNewTag(newTag);
    }
}
