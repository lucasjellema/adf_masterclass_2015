package nl.amis.speedyjoes.view;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;

import javax.inject.Inject;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import nl.amis.speedyjoes.business.MealOrderHandler;
import nl.amis.speedyjoes.common.Meal;
import nl.amis.speedyjoes.common.MealOrder;
import nl.amis.speedyjoes.common.log.ConversationLogger;
import nl.amis.speedyjoes.common.log.LogEntry;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@WebServlet(name = "OrderMeal", urlPatterns = { "/ordermeal" })
public class OrderMeal extends HttpServlet {
    @SuppressWarnings("compatibility:-1874115846715258745")
    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    @EJB(name = "MealOrderHandler")
    private MealOrderHandler mealOrderHandler;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // the identifier of the restaurant table
        String tableNumber = "";
        try {
            tableNumber = request.getParameter("tableNumber");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tableNumber == null || tableNumber.length() == 0) {
            tableNumber = "42";
        }

        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        writePage(tableNumber, out, false,null,null);
        out.close();
    }

    private void writePage(String tableNumber, PrintWriter out, boolean afterOrder, Meal meal, String traceJson) {
        out.println("<html>");
        out.println("<head><title>OrderMeal</title>" +
                    "<script type=\"text/javascript\">\n" + 
                    "function waiter(form) {\n" + 
        "document.body.style.cursor='wait';\n" + 
                    
         "document.forms[\"mealOrderForm\"].submit();      "+    
                    "}\n" + 
                    "    var dinner = [];\n" +
                    "</script> <script type=\"text/javascript\" src=\"speedyjoes.js\"></script>" +
                    "</head>");
        out.println("<body "+ (afterOrder?" onLoad=\"handleMealItemObjectDelivery(mealItem);\"" :"")+">");
        out.println("     <h1>Speedy Joe's Restaurant</h1>\n" + 
        "       <div id=\"output\"></div> \n" + 
        "        <div id=\"message\">Please place your order</div>\n" + 
        "        <br/>\n" + 
        "        <table>\n" + 
        "        <tr>\n" + 
        "        <td>\n" + 
        "        <img src=\"images/restaurantTable.png\"/>\n" + 
        "        <div id=\"mealDiv\">Dinner is being served...</div>\n" + 
        "        <br/>\n" + 
        "        <br/>\n" + 
        "<p>Note: You are seated at table " + tableNumber + "</p>");
        if (afterOrder) {
            out.println("<p>Your order was received and processed</p>");

            out.println("<p>Your Meal</p>");
            out.println("<p>"+meal.getDrink()+"</p>");
            out.println("<p>"+meal.getAppetizer()+"</p>");
            out.println("<p>"+meal.getMain()+"</p>");
            out.println("<p>Your check total is currently at € " + meal.getCheckTotal() + ".</p>");
            out.println("<br/><br/><br/><br/>");
//            out.println(
//            "        <div id=\"trace5\">\n" + 
//            "Execution Trace<br/><br/>    \n" + 
//            "        \n" + 
//            "        </div>\n"  
//
//                        );
            out.println("<script type=\"text/javascript\">\n" + 
            "    var mealItem =  "+traceJson+";\n" +
                        
            "</script>\n"
                        );
        }
        writeForm(out, tableNumber);

        out.println(     
        "</td>\n" + 
        "        <td id=\"trace\">\n" + 
        "Execution Trace<br/><br/>    \n" + 
        "        \n" + 
        "        </td>\n" + 
        "        </tr>\n" + 
        "        </table>\n");
        out.println("</body></html>");
    }

    private void writeForm(PrintWriter out, String tableNumber) {
         out.println("        <form name=\"mealOrderForm\"  action=\"ordermeal?tableNumber=\""+tableNumber+"\" method=\"POST\">\n" + 
        "        <table>\n" + 
        "            <tr>\n" + 
        "            <td>\n" + 
        "            Table:</td><td>\n" + 
        "            <input type=\"text\" name=\"table\" id=\"table\" value=\""+tableNumber+"\"/>\n" + 
        "            </td></tr>\n" + 
        "            <tr>\n" + 
        "            <td>\n" + 
        "            Drink:</td><td>\n" + 
        "            <input type=\"text\" name=\"drink\" id=\"drink\"/>\n" + 
        "            </td></tr>\n" + 
        "            <tr><td>\n" + 
        "            Appetizer:</td><td>\n" + 
        "            <input type=\"text\" name=\"appetizer\" id=\"appetizer\"/>\n" + 
        "            </td></tr><tr><td>\n" + 
        "            Main:\n" + 
        "            </td><td>\n" + 
        "            <input type=\"text\" name=\"main\" id=\"main\"/>\n" + 
        "            </td></tr></table>\n" + 
        "            <input type=\"button\" value=\"Place Order\" onclick=\"javascript:waiter('a');\"/>\n" + 
        "        </form>\n" ); 

    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // the identifier of the restaurant table
        ConversationLogger conversationLogger = new ConversationLogger();

        String drink = request.getParameter("drink");
        String main = request.getParameter("main");
        String appetizer = request.getParameter("appetizer");
        String tableNumber = request.getParameter("table");
        MealOrder order = new MealOrder(drink, appetizer, main, tableNumber);

        Meal meal = new Meal();
        meal.setMealOrder(order);
        sleep(600);
        // prepare drink
        meal.setDrink("Glass of " + drink.toUpperCase());
        meal.addToCheckTotal(4.80f); // for drink
        conversationLogger.enterLog("Waiters Station", 1, "Prepared drink", 600);
        // bring meal order to mid office
        sleep(400);
        String mealOrderHandlerResponse = "";
        try {
            mealOrderHandler.handleMealOrder(meal, conversationLogger, 2);
System.out.println("meal cooked "+meal.getMain()+meal.getAppetizer()+meal.getCheckTotal());
        } catch (Exception e) {
            e.printStackTrace();
            mealOrderHandlerResponse = e.getMessage();
            conversationLogger.enterLog("Waiters Station", 1, "Exception "+e.getMessage(), 400);
        }
        conversationLogger.enterLog("Waiters Station", 1, "Handed off meal order to mid office", 400);


        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        
        String json="{}";
        try {
            JSONObject r = new JSONObject().put("appetizerOrMain", "D");
            r.put("menuItem", meal.getDrink());
            r.put("price", meal.getCheckTotal());
        //            r.put("duration", event.getDuration());
            r.put("trace", conversationLogger.toJSON());
             json = r.toString();
            System.out.println("response=" + r);
        } catch (JSONException e) {
        }
        writePage(tableNumber, out, true, meal, json);

      

//        out.println("<p>Meal Order Processing Trail</p>");
//
//        for (LogEntry entry:conversationLogger.getLogEntries()){
//            out.println("<p>" + entry.getComponent()+" - "+entry.getDescription()+" - "+entry.getDurationInMiliSeconds()+" ms (thread: "+entry.getThreadId()+")</p>");
//            
//        }
//
//        out.println("<p>" + mealOrderHandlerResponse + "</p>");
//
//        out.println("<br/><br/><br/><br/>");
//        writeForm(out, tableNumber);
        out.println("</body></html>");
        out.close();
    }

    private void sleep(int delayInMilliSeconds) {
        try {
            Thread.sleep((delayInMilliSeconds)); //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}
