package nl.amis.hrm.view;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import oracle.adf.view.rich.activedata.ActiveModelContext;
import oracle.adf.view.rich.activedata.BaseActiveDataModel;
import oracle.adf.view.rich.event.ActiveDataEntry;
import oracle.adf.view.rich.event.ActiveDataUpdateEvent;

import oracle.adfinternal.view.faces.activedata.ActiveDataEventUtil;


public class ActiveMessageBean  extends BaseActiveDataModel implements ActiveMessageBeanI {
  private MessageManager msgMgr;
    private String message;


    @PostConstruct
  public void setupActiveData()
  {
    ActiveModelContext context = ActiveModelContext.getActiveModelContext();
    Object[] keyPath = new String[0];
    context.addActiveModelInfo(this, keyPath, "activemessage");
    msgMgr.registerMessageListener(this);
  }

  public void triggerDataUpdate(String message)
  {
    this.message = message;  
    counter.incrementAndGet();
    ActiveDataUpdateEvent event =
      ActiveDataEventUtil.buildActiveDataUpdateEvent(
        ActiveDataEntry.ChangeType.UPDATE,
        counter.get(),
        new String[0],
        null,
        new String[] { "activemessage" },
        new Object[] { message });

    fireActiveDataUpdate(event);
  }


  public String getState()
  {
    return message;
  }

  protected void startActiveData(
     Collection<Object> rowKeys, int startChangeCount){}

  protected void stopActiveData(
     Collection<Object> rowKeys) {}

  public int getCurrentChangeCount()
  {
    return counter.get();
  }
  private final AtomicInteger counter = new AtomicInteger(0);

    public void setMsgMgr(MessageManager msgMgr) {
        this.msgMgr = msgMgr;
    }

    public MessageManager getMsgMgr() {
        return msgMgr;
    }


}

