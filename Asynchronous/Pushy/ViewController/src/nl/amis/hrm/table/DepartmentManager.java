package nl.amis.hrm.table;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import javax.faces.context.FacesContext;
import oracle.adf.view.rich.event.ActiveDataEntry;
import oracle.adf.view.rich.event.ActiveDataUpdateEvent;
import oracle.adf.view.rich.model.ActiveCollectionModelDecorator;
import oracle.adf.view.rich.model.ActiveDataModel;

import oracle.adfinternal.view.faces.activedata.ActiveDataEventUtil;


import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.SortableModel;

public class DepartmentManager extends ActiveCollectionModelDecorator implements IBackendListener
{
    private CustomActiveModel _activeDataModel = new CustomActiveModel();
    private CollectionModel _model = null;

    
    public CollectionModel getCollectionModel() {
       if (_model == null) {
         DepartmentBackend context = getDepartmentBackend();  
          _model = new SortableModel(context.getDepartments());        
       }
       return _model;
     }

    private DepartmentBackend getDepartmentBackend() {
      FacesContext ctx = FacesContext.getCurrentInstance();
      ExpressionFactory ef = ctx.getApplication().getExpressionFactory();  
      ValueExpression ve = ef.createValueExpression(ctx.getELContext(), "#{DepartmentBackend}", DepartmentBackend.class);  
      DepartmentBackend context = (DepartmentBackend)ve.getValue(ctx.getELContext());
        return context;
    }

    public void onDeptartmentUpdate(Integer rowKey, Department dept)
    {
      if (rowKey != null) {
          
      CustomActiveModel asm = getMyActiveDataModel();
      
      // start the preparation for the ADS update
      asm.prepareDataChange();

      // create an ADS event, using an _internal_ util.....
      // this class is not part of the API
      ActiveDataUpdateEvent event = null;
      if (dept.getValue()!=null) {
        dept.setValue(dept.getValue()+100);
        System.out.println("changeEvent "+rowKey);  
      event=
        ActiveDataEventUtil.buildActiveDataUpdateEvent(
        ActiveDataEntry.ChangeType.UPDATE, // type
        asm.getCurrentChangeCount(), // changeCount
        new Object[] {rowKey}, // rowKey
        null, //insertKey (null as we don't insert stuff;
        new String[] {"name", "value"}, // attribue/property name that changes
        new Object[] { dept.getName(), dept.getValue()}   // the payload for the above attribute
        );
      }
      else  // new department
      {
          dept.setValue(dept.id+100);
        event = 
          ActiveDataEventUtil.buildActiveDataUpdateEvent(
          ActiveDataEntry.ChangeType.INSERT_AFTER, // type
          asm.getCurrentChangeCount(), // changeCount
          new Object[] {dept.id}, // rowKey
          
          // insert at ...
          new Object[] {dept.id-1},

        new String[] {"id","name", "value"}, // attribue/property name that changes
        new Object[] { dept.id, dept.getName(), dept.getValue()}   // the payload for the above attribute
          );
      }
      
      // deliver the new Event object to the ADS framework
      asm.notifyDataChange(event);
      }
    }

    public ActiveDataModel getActiveDataModel() {
         return _activeDataModel;
    }

    public CustomActiveModel getMyActiveDataModel() {
        return _activeDataModel;
    }

}


