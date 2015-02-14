/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sample.whiteboardapp;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author oracle
 */
@Singleton
@Startup


@ServerEndpoint("/whiteboardendpoint2")
public class TimeEventObserver {
    
     public void onTimeEvent(@Observes @WBTimeEvent TimeEvent msg) {
         System.out.println("TimeEVent!");
     
     }

     private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    @OnMessage
    public String onMessage(String message, Session session) {
        System.out.println("message received " + message);
        for (Session peer : peers) {
            if (!peer.equals(session)) {
                try {
                    peer.getBasicRemote().sendText(message + " - retweet");
                } catch (IOException ex) {
                    Logger.getLogger(TimeEventObserver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return "BOEHOE";
    }

    @OnOpen
    public void onOpen(Session peer) {
        peers.add(peer);
    }
     
       @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);
    }

}
