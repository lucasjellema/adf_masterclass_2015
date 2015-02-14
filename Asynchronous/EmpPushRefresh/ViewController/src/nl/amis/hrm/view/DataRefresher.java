package nl.amis.hrm.view;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

public class DataRefresher {
    
    
  public void refreshDataFromTable(ClientEvent clientEvent) {
    String tablename = (String)clientEvent.getParameters().get("tablename");
    System.out.println("table to refresh "+tablename);
        BindingContext bindingContext = BindingContext.getCurrent();
        BindingContainer bindingContainer = bindingContext.getCurrentBindingsEntry();
        // refresh EmployeesView1Iterator
        if (bindingContainer instanceof DCBindingContainer) {
            DCIteratorBinding cIteratorBinding = ((DCBindingContainer)bindingContainer).findIteratorBinding("EmployeesView1Iterator");
          cIteratorBinding.getViewObject().clearCache();
          System.out.println("Clear Cache for VO EmployeesView1");
        } else {
          OperationBinding binding = bindingContainer.getOperationBinding("RequeryEmployeesView");
          System.out.println("Excute binding "+binding.getName());
          
          binding.execute();
          
        }
      // refresh SumSalariesPerDepartmentView1Iterator
      if (bindingContainer instanceof DCBindingContainer) {
          DCIteratorBinding cIteratorBinding = ((DCBindingContainer)bindingContainer).findIteratorBinding("SumSalariesPerDepartmentView1Iterator");
        cIteratorBinding.getViewObject().clearCache();
        System.out.println("Clear Cache for VO SumSalariesPerDepartmentView1");
      } 

    }

}
