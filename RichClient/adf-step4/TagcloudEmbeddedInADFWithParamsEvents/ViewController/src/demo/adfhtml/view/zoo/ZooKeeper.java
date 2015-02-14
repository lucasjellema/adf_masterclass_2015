package demo.adfhtml.view.zoo;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import oracle.adf.view.rich.context.AdfFacesContext;

import org.apache.myfaces.trinidad.util.ComponentReference;

public class ZooKeeper {

List<String> animals = new ArrayList<String>();

    private ComponentReference zooUIComponent;
//keep track of all selected animals
public void addAnimal(String animal) {
    animals.add(animal);
    // refresh zoo component
    AdfFacesContext.getCurrentInstance().addPartialTarget(getZooUIComponent());

}

    public void setAnimals(List<String> animals) {
        this.animals = animals;
    }

    public List<String> getAnimals() {
        return animals;
    }
    
    public void resetZoo() {
        animals.clear();
    }

    public UIComponent getZooUIComponent() {
        return zooUIComponent == null ? null :
               zooUIComponent.getComponent();
    }

    public void setZooUIComponent(UIComponent zooComponent) {
        zooUIComponent =
                ComponentReference.newUIComponentReference(zooComponent);
    }


}
