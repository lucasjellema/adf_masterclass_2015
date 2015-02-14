package demo.adfhtml.view.events;

import oracle.adf.share.logging.ADFLogger;

/*
 * This class is used to create a POJO Data Control that is used to publish an event with a simple payload that does not
 * require any preprocessing before being published.
 */
public class EventPublisher {
    ADFLogger _logger = ADFLogger.createADFLogger(this.getClass());
    public Object publishEvent(Object payload) {
        _logger.warning("publisher bean has been invoked with payload "+payload);
        return payload;
    }
}
