package nl.amis.autoppr;

/****
 * based on:  http://www.adfplus.com/2013/05/automatic-partial-page-rendering-across.html
 *
 ***/

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import oracle.adf.view.rich.activedata.ActiveModelContext;
import oracle.adf.view.rich.activedata.BaseActiveDataModel;
import oracle.adf.view.rich.event.ActiveDataEntry;
import oracle.adf.view.rich.event.ActiveDataUpdateEvent;

import oracle.adfinternal.view.faces.activedata.ActiveDataEventUtil;

public class AutoPPRSupport extends BaseActiveDataModel {
    private static final Object[] STATIC_KEY = { 0 };

    private final AtomicInteger changeCounter = new AtomicInteger();

    protected void startActiveData(Collection<Object> collection, int i) {
    }

    protected void stopActiveData(Collection<Object> collection) {
    }

    public int getCurrentChangeCount() {
        return changeCounter.get();
    }

    /**
     * Find out which component is currently evaluating EL to get at this attribute and remember the component for auto PPR.
     *
     * @param attributeName the name of the current attribute
     */
    public void rememberConsumerForAttribute(String attributeName) {
        ActiveModelContext.getActiveModelContext().addActiveModelInfo(this, STATIC_KEY, attributeName);
    }

    /**
     * Refresh all components that use the attribute <code>attributeName</code>
     * @param attributeName
     * @param newValue
     */
    public void notifyConsumers(String attributeName, Object newValue) {
        ActiveDataUpdateEvent event =
            ActiveDataEventUtil.buildActiveDataUpdateEvent(ActiveDataEntry.ChangeType.UPDATE,
                                                           changeCounter.incrementAndGet(), STATIC_KEY, null, new String[] {
                                                           attributeName }, new Object[] { newValue });
        fireActiveDataUpdate(event);
    }
}
