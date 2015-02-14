/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amis.speedyjoes.view.push;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


import nl.amis.speedyjoes.common.log.ConversationLogger;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import org.sample.whiteboardapp.TimeEvent;
import org.sample.whiteboardapp.WBTimeEvent;


@Singleton
@ServerEndpoint("/mediatorendpoint")
public class SocketMediator {

    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    private static Map<String, Session> peerTableMap = Collections.synchronizedMap(new HashMap<String,Session>());

    @OnMessage
    public String onMessage(String message, Session session) {
        System.out.println("try to open websocket");
        
        try {
            JSONObject jObj = new JSONObject(message);
            String tableNumber= (String) jObj.get("tableNumber");
            System.out.println("opened websocket channel with table "+tableNumber);
            // link up session with tableNumber so we know which session to inform when a meal item ready event occurs for that table
            peerTableMap.put(tableNumber, session);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "message was received and processed: "+message;
    }

    @OnOpen
    public void onOpen(Session peer) {
        peers.add(peer);
    }

    public void onTimeEvent(@Observes @WBTimeEvent TimeEvent event) {
        for (Session peer : peers) {
            try {
                peer.getBasicRemote().sendText("Time event: " + event.getTimestamp());
            } catch (IOException ex) {
                Logger.getLogger(SocketMediator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }// onTimeEvent

 
    public void onMealItemReadyEvent(@Observes @JoesMealItemReadyEvent MealItemReadyEvent event) {
        System.out.println("Meal Item Ready Event observed by SocketMediator " + event.getAppetizer()+" for table "+event.getTableNumber());
        ConversationLogger logger = new ConversationLogger(event.getJsonTrace());
        logger.enterLog("Waiter", logger.getHighestLevel()+1, "Taken cooked meal item to table ", 500);

        String json="{}";
        try {
            JSONObject r = new JSONObject().put("appetizerOrMain", event.getAppetizerOrMain());
            r.put("menuItem", event.getMenuItem());
            r.put("price", event.getPrice());
            r.put("duration", event.getDuration());
            r.put("trace", logger.toJSON());
             json = r.toString();
            System.out.println("response=" + r);
        } catch (JSONException e) {
        }

        
        if (peerTableMap.containsKey(event.getTableNumber())) {
        Session s = peerTableMap.get(event.getTableNumber());
            try {
                s.getBasicRemote().sendText(json );
                System.out.println("send meal item to table "+event.getTableNumber());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Could not find table socket for meal item ");
        }
    }//onMailItemReadyEvent

    
    @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);
    }
}

