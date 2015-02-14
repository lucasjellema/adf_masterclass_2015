package demo.adfhtml.view.events;

import demo.adfhtml.view.ADFHelper;
import demo.adfhtml.view.MatchHelperBean;

import oracle.adf.share.logging.ADFLogger;

/*
 * This class is used to create a POJO DataControl to consume the 'tag selection has changed'  event.
 * The new tag selection is the payload for the event. This selection is passed on to the matchHelperBean in the current taskflow's pageflowscope
 */
public class EventConsumer {
    ADFLogger _logger = ADFLogger.createADFLogger(this.getClass());
    public void handleEvent(Object payload) {
        _logger.warning("Event consumed - payload =  "+payload);
        MatchHelperBean mhb = (MatchHelperBean)ADFHelper.evaluateEL("#{pageFlowScope.matchHelperBean}");
        String filteringTags = (String)payload;
        _logger.warning(" Match Helper Bean to be invoked "+mhb);
        mhb.setFilteringTags(filteringTags);
    }
}
