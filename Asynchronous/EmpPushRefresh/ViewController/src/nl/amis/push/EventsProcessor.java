package nl.amis.push;

import java.util.ArrayList;
import java.util.List;

public class EventsProcessor {

    static List<MyApplicationEventListener> listeners = new ArrayList<MyApplicationEventListener>();

    public static void registerEventListener( MyApplicationEventListener listener) {
        if (!listeners.contains(listener))
        listeners.add(listener);
    }
    
    public static void publishEvent(MyEvent event) {
        for (MyApplicationEventListener listener:listeners) {
            System.out.println("publish event to listener "+listener);
            listener.processEvent(event);
        }
    }

}
