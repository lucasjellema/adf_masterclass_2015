package demo.adfhtml.view.parkinglot;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import oracle.adf.view.rich.context.AdfFacesContext;

import org.apache.myfaces.trinidad.util.ComponentReference;

public class ParkingAttendant {
    List<String> cars = new ArrayList<String>();

        private ComponentReference parkinglotUIComponent;
    //keep track of all selected animals
    public void addCar(String car) {
        cars.add(car);
        // refresh parkinglot component
        AdfFacesContext.getCurrentInstance().addPartialTarget(getParkinglotUIComponent());

    }

        public void setParkedCars(List<String> cars) {
            this.cars = cars;
        }

        public List<String> getParkedCars() {
            return cars;
        }
        
        public void resetParkingLot() {
            cars.clear();
        }

        public UIComponent getParkinglotUIComponent() {
            return parkinglotUIComponent == null ? null :
                   parkinglotUIComponent.getComponent();
        }

        public void setParkinglotUIComponent(UIComponent parkinglotComponent) {
            parkinglotUIComponent =
                    ComponentReference.newUIComponentReference(parkinglotComponent);
        }


    }
