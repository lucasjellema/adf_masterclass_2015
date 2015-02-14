package nl.amis.speedyjoes.view;

import javax.ejb.MessageDriven;

import javax.enterprise.event.Event;

import javax.inject.Inject;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import javax.jms.MessageListener;

import nl.amis.speedyjoes.common.log.ConversationLogger;
import nl.amis.speedyjoes.view.push.JoesMealItemReadyEvent;

import nl.amis.speedyjoes.view.push.MealItemReadyEvent;

import org.sample.whiteboardapp.TimeEvent;
import org.sample.whiteboardapp.WBTimeEvent;


@MessageDriven(mappedName = "jms/JoesQueue")
public class WaiterStationJMSListener implements MessageListener {
    @Inject
    @JoesMealItemReadyEvent
    Event<MealItemReadyEvent> mealItemReadyEvent;

    public WaiterStationJMSListener() {
        super();
    }

    @Override
    public void onMessage(Message message) {
        try {
            // In JMS 1.1:
            MapMessage mapMessage = (MapMessage) message;
            String trace = mapMessage.getString("trace");
            ConversationLogger logger = new ConversationLogger(trace);
            logger.enterLog("Waiters Station", logger.getHighestLevel() + 1,
                            "Received cooked meal item, passed on to a waiter", 400);
            MealItemReadyEvent event = new MealItemReadyEvent();
            event.setMenuItem(mapMessage.getString("menuItem"));
            event.setAppetizerOrMain(mapMessage.getString("AorM"));
            event.setTableNumber(mapMessage.getString("tableNumber"));
            event.setPrice(mapMessage.getFloat("price"));
            event.setDuration(mapMessage.getInt("duration"));
            event.setJsonTrace(logger.toJSON().toString());
            mealItemReadyEvent.fire(event);
        } catch (JMSException e) {
            System.err.println("Error while fetching message payload: " + e.getMessage());
        }
    }
}
