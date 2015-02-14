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

@WebServlet(name = "OrderMeal", urlPatterns = { "/ordermeal" })
public class OrderMeal extends HttpServlet {
    @SuppressWarnings("compatibility:-1874115846715258745")
    private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    @EJB(name = "MealOrderHandlerAsynch")
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
        out.println("<html>");
        out.println("<head><title>OrderMeal</title>" +
                    "<script type=\"text/javascript\">\n" + 
                    "function waiter(form) {\n" + 
        "document.body.style.cursor='wait';\n" + 
                    
        "document.body.style.cursor = \"http://wiki-devel.sugarlabs.org/images/e/e2/Arrow.cur\";"+
         "document.forms[\"mealOrderForm\"].submit();      "+    
                    "}\n" + 
                    "</script>" +
                    "</head>");
        out.println("<body >");
        out.println("<p>Welcome at Speedy Joe's</p>");
        out.println("<p>Note: You are seated at table " + tableNumber + "</p>");
        writeForm(out, tableNumber);
        out.println("</body></html>");
        out.close();
    }

    private void writeForm(PrintWriter out, String tableNumber) {
        out.println(" <form name=\"mealOrderForm\"  action=\"ordermeal?tableNumber="+tableNumber+"\" method=\"POST\">\n" +
        "Drink: <input type=\"text\" name=\"drink\" />\n"   + "<br />\n" +
                    "Appetizer: <input type=\"text\" name=\"appetizer\">\n" + "<br />\n" +
                    "Main: <input type=\"text\" name=\"main\" />\n"  + "<br />\n" 
                    + "<input type=\"button\" value=\"Place Order\"  onclick=\"javascript:waiter('a');\"   />\n" +
                    "</form>\n");

    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // the identifier of the restaurant table
        String tableNumber = "";
        try {
            tableNumber = request.getParameter("tableNumber");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConversationLogger conversationLogger = new ConversationLogger();

        String drink = request.getParameter("drink");
        String main = request.getParameter("main");
        String appetizer = request.getParameter("appetizer");
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
        } catch (Exception e) {
            mealOrderHandlerResponse = e.getMessage();
        }
        conversationLogger.enterLog("Waiters Station", 1, "Handed off meal order to mid office", 400);


        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>OrderMeal</title></head>");
        out.println("<body>");
        out.println("<p>Your order was received and processed</p>");

        out.println("<p>Your Meal</p>");
        out.println("<p>"+meal.getDrink()+"</p>");
        out.println("<p>"+meal.getAppetizer()+"</p>");
        out.println("<p>"+meal.getMain()+"</p>");
        out.println("<p>Your check total is currently at € " + meal.getCheckTotal() + ".</p>");
        out.println("<br/><br/><br/><br/>");


        out.println("<p>Meal Order Processing Trail</p>");

        for (LogEntry entry:conversationLogger.getLogEntries()){
            out.println("<p>" + entry.getComponent()+" - "+entry.getDescription()+" - "+entry.getDurationInMiliSeconds()+" ms (thread: "+entry.getThreadId()+")</p>");
            
        }

        out.println("<p>" + mealOrderHandlerResponse + "</p>");

        out.println("<br/><br/><br/><br/>");
        writeForm(out, tableNumber);
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
