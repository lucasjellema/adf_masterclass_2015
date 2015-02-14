package nl.amis.hrm.view;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.faces.event.ActionEvent;

public class LongRunningJobCoordinator implements Runnable{
    
    private ActiveBean activeBean;
    private String potentiallyBigPayload;
    
    public void runBigJob(ActionEvent ae) {
      // start job in parallel thread
      // pass in the activeBean as the object to inform of the progress of the big job
      
      ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
      ses.schedule
      ( this
       , 3 // let's wait one second before starting the job
       , TimeUnit.SECONDS
        );

      // then complete the synchronous request
      
    }


  public void run() {
    activeBean.triggerDataUpdate("Start - 0 %");
    // normally you would do the real work such as processing the big file here
    for (int i=0;i<10;i++) { // sleep between 2 and 4 seconds
            try {
                Thread.sleep(((Double)((2+2* Math.random())*  1000)).longValue());
            } catch (InterruptedException e) {
            }
            activeBean.triggerDataUpdate((i+1)*10+" %");
        System.out.println("Job Progress "+(i+1)*10+" %");
    }
    activeBean.triggerDataUpdate("Job Done - 100 %");
    
  }
 

    public ActiveBean getActiveBean() {
        return activeBean;
    }

    public void setActiveBean(ActiveBean activeBean) {
        this.activeBean = activeBean;
    }

    public void setPotentiallyBigPayload(String potentiallyBigPayload) {
        this.potentiallyBigPayload = potentiallyBigPayload;
    }

    public String getPotentiallyBigPayload() {
        return potentiallyBigPayload;
    }
}
