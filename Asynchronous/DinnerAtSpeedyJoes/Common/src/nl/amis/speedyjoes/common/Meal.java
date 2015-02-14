package nl.amis.speedyjoes.common;

public class Meal {
    private String drink;
    private String appetizer;
    private String main;
    private MealOrder mealOrder;
    private Float checkTotal = 0f;

    public void setCheckTotal(Float checkTotal) {
        this.checkTotal = checkTotal;
    }

    public Float getCheckTotal() {
        return checkTotal;
    }

    public void addToCheckTotal(Float amount) {
        checkTotal = checkTotal + amount;
    }

    public void setMealOrder(MealOrder mealOrder) {
        this.mealOrder = mealOrder;
    }

    public MealOrder getMealOrder() {
        return mealOrder;
    }

    public Meal(String drink, String appetizer, String main) {
        super();
        this.drink = drink;
        this.appetizer = appetizer;
        this.main = main;
    }

    public Meal(MealOrder mealOrder) {
        super();
        this.drink = drink;
        this.appetizer = appetizer;
        this.main = main;
    }


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

    public Meal() {
        super();
    }
}
