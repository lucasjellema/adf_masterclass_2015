package nl.amis.speedyjoes.view.push;

import nl.amis.speedyjoes.common.MealOrder;

public class MealItemReadyEvent {

    private String appetizerOrMain;
    private String menuItem;
    private String appetizer;
    private String main;
    private String tableNumber;
    private String jsonTrace;
    private Integer duration;

    public void setJsonTrace(String jsonTrace) {
        this.jsonTrace = jsonTrace;
    }

    public String getJsonTrace() {
        return jsonTrace;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDuration() {
        return duration;
    }


    private Float checkTotal = 0f;
    private Float price = 0f;

    public void setAppetizerOrMain(String appetizerOrMain) {
        this.appetizerOrMain = appetizerOrMain;
    }

    public String getAppetizerOrMain() {
        return appetizerOrMain;
    }

    public void setMenuItem(String menuItem) {
        this.menuItem = menuItem;
    }

    public String getMenuItem() {
        return menuItem;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getPrice() {
        return price;
    }

    public void setAppetizer(String appetizer) {
        this.appetizer = appetizer;
    }

    public String getAppetizer() {
        return appetizer;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getMain() {
        return main;
    }


    public void setCheckTotal(Float checkTotal) {
        this.checkTotal = checkTotal;
    }

    public Float getCheckTotal() {
        return checkTotal;
    }

    public MealItemReadyEvent() {
        super();
    }
}
