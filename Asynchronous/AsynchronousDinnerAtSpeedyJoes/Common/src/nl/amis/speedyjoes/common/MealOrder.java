package nl.amis.speedyjoes.common;

public class MealOrder {
    
    private String drink;
    private String appetizer;
    private String tableNumber;

    public MealOrder(String drink, String appetizer, String main, String tableNumber) {
        super();
        this.drink = drink;
        this.appetizer = appetizer;
        this.main = main;
        this.tableNumber = tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableNumber() {
        return tableNumber;
    }
    private String main;

    public void setDrink(String drink) {
        this.drink = drink;
    }

    public String getDrink() {
        return drink;
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

    public MealOrder() {
        super();
    }
}
