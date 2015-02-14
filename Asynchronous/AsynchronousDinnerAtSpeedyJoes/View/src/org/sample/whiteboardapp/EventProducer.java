/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sample.whiteboardapp;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.enterprise.event.Event;
import javax.inject.Inject;


/**
 *
 * @author oracle
 */

@Stateless
public class EventProducer  {
@Inject  @WBTimeEvent Event<TimeEvent> timeEvent;



    @Schedule(second="*/3", minute="*", hour="*", info="Time Event publisher")
    public void produceRegularEvent(Timer t) {
        System.out.println("Call # " );
        TimeEvent event = new TimeEvent();
        timeEvent.fire(event);        
    }
        
}
