package nl.amis.speedyjoes.business;

import java.math.BigDecimal;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;

import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import javax.sql.DataSource;

import nl.amis.speedyjoes.common.Meal;
import nl.amis.speedyjoes.common.MealOrder;
import nl.amis.speedyjoes.common.log.ConversationLogger;

@Stateless(name = "MealOrderHandlerAsynch", mappedName = "DinnerAtSpeedyJoes-Model-MealOrderHandlerAsynch")
public class MealOrderHandlerBean implements MealOrderHandler, MealOrderHandlerLocal {
    @Resource
    SessionContext sessionContext;

    @Resource(lookup = "jdbc/joesRestaurant")
    private DataSource dataSource;

    public MealOrderHandlerBean() {
    }

    @Override
    public void handleMealOrder(Meal meal, ConversationLogger conversationLogger, int level) {
        sleep(700);
        conversationLogger.enterLog("Mid Office", level, "Processed Meal Order, passed on to Kitchen", 700);
        try {
           haveKitchenPrepareMeal(meal, conversationLogger, level + 1);
        }
        catch (Exception e) {
            conversationLogger.enterLog("Mid Office", level, "Exception: "+e.getMessage(),
                                        1);
            
        }
        //        String result="Your order was received and processed; tableNumber="+tableNumber+" and your order total is € 131,21"+ doThis() ;
        return;
    }

    private void sleep(int delayInMilliSeconds) {
        try {
            Thread.sleep((delayInMilliSeconds)); //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void haveKitchenPrepareMeal(Meal meal, ConversationLogger conversationLogger, int level) {
        String result="";
        conversationLogger.start();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            // insert

            String insertKitchenOrderSQL =
                "INSERT INTO kitchen_orders" + "(table_number, menu_item, appetizer_or_main) VALUES" + "(?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertKitchenOrderSQL);
            //APPETIZER
            preparedStatement.setString(1, meal.getMealOrder().getTableNumber());
            preparedStatement.setString(2, meal.getMealOrder().getAppetizer());
            preparedStatement.setString(3, "A");
            // execute insert SQL stetement
            preparedStatement.executeUpdate();
            
            //MAIN
            preparedStatement.setString(1, meal.getMealOrder().getTableNumber());
            preparedStatement.setString(2, meal.getMealOrder().getMain());
            preparedStatement.setString(3, "M");
            // execute insert SQL stetement
            preparedStatement.executeUpdate();

            result="jobs submitted";
            
            
            // in the asynchronous case, the following is very optimistic (if not pointless)
            // fetch the prepared dishes
            PreparedStatement sql =
                conn.prepareStatement("SELECT menu_item, extract( second from (end_time - start_time)) as preparation_duration, price , upper(APPETIZER_OR_MAIN) as aOrM " +
                                      "from kitchen_orders where table_number = ?");
            sql.setString(1, meal.getMealOrder().getTableNumber());
            ResultSet set = sql.executeQuery();
            result="queried";
            
            while (set.next()) {
                result = "query result found";
                String menuItem = set.getString("menu_item");
                String appetizerOrMain = set.getString("aOrM");
                Object price = set.getObject("price");
                Object duration = set.getObject("preparation_duration");

                if ("A".equals(appetizerOrMain)) {
                    conversationLogger.enterLog("Kitchen", level, "Prepared Appetizer", Math.round(1000*((BigDecimal)duration).floatValue()));
                    meal.setAppetizer("Saucer with " + menuItem );
                    meal.addToCheckTotal(((BigDecimal)price).floatValue());
                }

                if ("M".equals(appetizerOrMain)) {
                    conversationLogger.enterLog("Kitchen", level, "Prepared Main Course", Math.round(1000*((BigDecimal)duration).floatValue()));
                    meal.setMain("Plate with " + menuItem);
                    meal.addToCheckTotal(((BigDecimal)price).floatValue());
                }
            }//while
            result="done set processing";
        }


        catch (SQLException ex) {
            //EXCEPTION HANDLING
            result = ex.getMessage();
        }
        catch (Exception ex) {
            //EXCEPTION HANDLING
            result = "general exception "+result+" :" +ex.getMessage();
        }finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                result = ex.getMessage();
            }
        }
        result="before final enterlog";
        conversationLogger.enterLog("Kitchen", level, "Actual DB interaction: " + result, conversationLogger.stop());

    }
}
