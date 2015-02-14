package demo.adfhtml.view.parkinglot;

import demo.adfhtml.view.ADFHelper;


public class CarTagClickedEventConsumer {
    public void handleEvent(Object payload) {
        ParkingAttendant pa = (ParkingAttendant)ADFHelper.evaluateEL("#{viewScope.parkingattendant}");
        String selectedTag = (String)payload;
        pa.addCar(selectedTag);
    }}
