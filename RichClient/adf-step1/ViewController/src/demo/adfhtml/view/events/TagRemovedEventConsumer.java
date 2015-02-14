package demo.adfhtml.view.events;

import demo.adfhtml.view.ADFHelper;
import demo.adfhtml.view.MatchDetailsHelperBean;

import oracle.adf.share.logging.ADFLogger;

public class TagRemovedEventConsumer {
    ADFLogger _logger = ADFLogger.createADFLogger(this.getClass());
    
    public TagRemovedEventConsumer() {
        super();
    }
    
    public void handleEvent(Object payload) {
        _logger.warning("tag rmeoved Event consumed - payload =  "+payload);

        _logger.warning("Event consumed - payload =  "+payload);
        MatchDetailsHelperBean mdhb = (MatchDetailsHelperBean)ADFHelper.evaluateEL("#{pageFlowScope.matchDetailsHelperBean}");
        String removedTag = (String)payload;
        _logger.warning(" Match Helper Bean to be invoked "+mdhb);
        mdhb.removeTag(removedTag);
    }
}
