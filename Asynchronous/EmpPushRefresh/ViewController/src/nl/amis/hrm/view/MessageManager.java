package nl.amis.hrm.view;

import java.util.ArrayList;
import java.util.List;

import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.render.ClientEvent;

public class MessageManager {
    private List<ActiveMessageBeanI> listeners = new ArrayList<ActiveMessageBeanI>();
    private String message;
    
    public void registerMessageListener( ActiveMessageBeanI amb) {
       listeners.add(amb);  
    }


    public void setMessage(String message) {
        this.message = message;
        for (ActiveMessageBeanI amb: listeners) {
            System.out.println("msgmgr - informing of message "+message);
          amb.triggerDataUpdate(message);
        }
    }

    public String getMessage() {
        return message;
    }


     List<ActiveMessageBeanI> getListeners() {
        return listeners;
    }
}
