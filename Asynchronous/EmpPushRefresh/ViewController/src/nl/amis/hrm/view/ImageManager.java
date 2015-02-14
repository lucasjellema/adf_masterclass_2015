package nl.amis.hrm.view;

import nl.amis.push.EventsProcessor;
import nl.amis.push.MyApplicationEventListener;
import nl.amis.push.MyEvent;


public class ImageManager extends MessageManager implements MyApplicationEventListener {

    public ImageManager () {
      EventsProcessor.registerEventListener(this);
    }

    public void processEvent(MyEvent event) {
      for (ActiveMessageBeanI amb: super.getListeners()) {
        amb.triggerDataUpdate(event.getPayload());
      }
    }
}
