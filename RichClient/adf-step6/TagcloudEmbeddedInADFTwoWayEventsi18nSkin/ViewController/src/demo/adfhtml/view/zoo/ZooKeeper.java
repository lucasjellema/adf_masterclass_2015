package demo.adfhtml.view.zoo;

import demo.adfhtml.view.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import javax.faces.component.UIComponent;

import javax.faces.event.ActionEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import org.apache.myfaces.trinidad.util.ComponentReference;

public class ZooKeeper {

    private List<String> animals = new ArrayList<String>();
    private String newAnimal;
    private ComponentReference zooUIComponent;

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
        return zooUIComponent == null ? null : zooUIComponent.getComponent();
    }

    public void setZooUIComponent(UIComponent zooComponent) {
        zooUIComponent =
                ComponentReference.newUIComponentReference(zooComponent);
    }

    public void addAnimalFromNewAnimal(ActionEvent ae) {
        BindingContext bindingContext = BindingContext.getCurrent();
        BindingContainer bindingContainer =
            bindingContext.getCurrentBindingsEntry();
        OperationBinding binding =
            bindingContainer.getOperationBinding("publishNewTagEvent");
        Map<String, Object> eventPayload = new HashMap<String, Object>();
        eventPayload.put("clientId", "zooCloud");
        eventPayload.put("tag", new Tag(getNewAnimal() , 25));
        binding.getParamsMap().put("payload", eventPayload);
        binding.execute();
        setNewAnimal("");
    }

    public void setNewAnimal(String newAnimal) {
        this.newAnimal = newAnimal;
    }

    public String getNewAnimal() {
        return newAnimal;
    }
}
