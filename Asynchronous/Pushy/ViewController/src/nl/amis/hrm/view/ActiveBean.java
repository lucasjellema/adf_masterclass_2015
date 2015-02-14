package nl.amis.hrm.view;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import oracle.adf.view.rich.activedata.ActiveModelContext;
import oracle.adf.view.rich.activedata.BaseActiveDataModel;
import oracle.adf.view.rich.event.ActiveDataEntry;
import oracle.adf.view.rich.event.ActiveDataUpdateEvent;

import oracle.adfinternal.view.faces.activedata.ActiveDataEventUtil;

public class ActiveBean extends BaseActiveDataModel
{
  @PostConstruct
  public void setupActiveData()
  {
    ActiveModelContext context = ActiveModelContext.getActiveModelContext();
    Object[] keyPath = new String[0];
    context.addActiveModelInfo(this, keyPath, "state");

    // setup a timer that queues the events (in a loop)
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    ses.scheduleAtFixedRate(
      new Runnable() {
        public void run() {
          triggerDataUpdate();
        }
      },
      5, // let's wait some seconds
      2, // period between the updates
      TimeUnit.SECONDS);
  }

  public void triggerDataUpdate()
  {
    counter.incrementAndGet();

    ActiveDataUpdateEvent event =
      ActiveDataEventUtil.buildActiveDataUpdateEvent(
        ActiveDataEntry.ChangeType.UPDATE,
        counter.get(),
        new String[0],
        null,
        new String[] { "state" },
        new Object[] { counter.get() });

    fireActiveDataUpdate(event);
  }

  public String getState()
  {
    return String.valueOf(counter);
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
}

