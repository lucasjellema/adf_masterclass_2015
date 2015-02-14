package nl.amis.hrm.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Timer;

import java.util.TimerTask;

import javax.annotation.PostConstruct;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import javax.faces.context.FacesContext;

import oracle.adf.view.rich.activedata.ActiveModelContext;
import oracle.adf.view.rich.event.ActiveDataEntry;
import oracle.adf.view.rich.event.ActiveDataUpdateEvent;
import oracle.adf.view.rich.model.ActiveCollectionModelDecorator;

import oracle.adf.view.rich.model.ActiveDataModel;

import oracle.adfinternal.view.faces.activedata.ActiveDataEventUtil;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.SortableModel;

public class ActiveMultiMessageBean 
  extends ActiveCollectionModelDecorator  implements ActiveMessageBeanI 
  {
     private CustomActiveModel _activeDataModel = new CustomActiveModel();
     private CollectionModel _model = null;
     private List<Message> messages = new ArrayList<Message>();



  private MessageManager msgMgr;

  // create daemon Timer
  private static final Timer _TIMER = new Timer(true);
  private ValueUpdater _timerTask;
  private Long _interval = 1500L;
  
  @PostConstruct
  public void setupActiveData()
  {
  msgMgr.registerMessageListener(this);
  

  }
  
  /**
   * Implementation of a timer task.
   */
  private class ValueUpdater extends TimerTask
  {
    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run()
    {
      msgMgr.setMessage("reaction to: "+messages.get(messages.size()-1).getMessage());
    }
  }
  
     public CollectionModel getCollectionModel() {
        if (_model == null) {
           _model = new SortableModel(messages);        
        }
        return _model;
      }
     
  public void triggerDataUpdate(String message)
  {
      System.out.println("Informed of new message "+message);
      // add message to messages
      Message msg = new Message();
      msg.setMessage(message);
      msg.setTimestamp(new Date());
      messages.add(msg);
//      _timerTask = new ValueUpdater();
//      _TIMER.schedule(_timerTask, _interval);
//      
      
    CustomActiveModel asm = _activeDataModel;
    
    // start the preparation for the ADS update
    asm.prepareDataChange();

    
    ActiveDataUpdateEvent event = 
      ActiveDataEventUtil.buildActiveDataUpdateEvent(
      ActiveDataEntry.ChangeType.INSERT_AFTER, // type
      asm.getCurrentChangeCount(), // changeCount
      new Object[] {messages.size()}, // rowKey
      
      // insert at ...
      new Object[] {messages.size()-1},

      new String[] {"message", "timestamp"}, // attribue/property name that changes
      new Object[] { msg.getMessage(), msg.getTimestamp()}   // the payload for the above attributes
      );
    // deliver the new Event object to the ADS framework
    System.out.println("Notify of new event");
    asm.notifyDataChange(event);
    }

  

    public void setMsgMgr(MessageManager msgMgr) {
        this.msgMgr = msgMgr;

    }

    public MessageManager getMsgMgr() {
        return msgMgr;
    }

  public ActiveDataModel getActiveDataModel() {
       return _activeDataModel;
  }

  public CustomActiveModel getCustomActiveDataModel() {
      return _activeDataModel;
  }
}
