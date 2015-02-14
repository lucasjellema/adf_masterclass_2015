package nl.amis.hrm.table;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;

public class DepartmentBackend {

  public void setListener(IBackendListener listener) {
      this.listener = listener;
  }

  public IBackendListener getListener() {
      return listener;
  }

  private IBackendListener listener;

    public DepartmentBackend() {
        departments.add(new Department("sales", 1L));
        departments.add(new Department("management", 2L));
        departments.add(new Department("support", 3L));
    }


    public Department inventNewDepartment() {
        Department d =new Department("sales", new Long(departments.size()+1)); 
      departments.add(d);
      return d;
    }

    private final List<Department> departments =
        new CopyOnWriteArrayList<Department>();

    public List<Department> getDepartments() {
        return departments;
    }

    public void changeData() {
        // Add event code here...
        System.out.println("changeData");
        List<Department> rows = getDepartments();
        Department dataRow = null;
        for (int rowKey = 0; rowKey < rows.size(); rowKey++) {
            dataRow = rows.get(rowKey);
        //    String newDepartmentNameValue = dataRow.getName();
        //    dataRow.setName(newDepartmentNameValue);
            listener.onDeptartmentUpdate(rowKey, dataRow);

            Long stoptime = 2000L;
            try {
                Thread.sleep(stoptime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
          Department d = inventNewDepartment();
        d.setValue(null);
          listener.onDeptartmentUpdate(rows.size()-1, d);

            

        }
    }

    @PostConstruct
    private void startUpdateProcess() {
        Runnable dataChanger = new Runnable() {
            public void run() {
                // wait 10 seconds before we start to update our stocks...
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // in an infinitive loop we trigger every 3 seconds a new data update...
                while (listener != null) {
                    try {
                        // wait 3 seconds
                        Thread.sleep(3000);
                        changeData();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        // start the process
        Thread newThread = new Thread(dataChanger);
        newThread.start();
    }
}