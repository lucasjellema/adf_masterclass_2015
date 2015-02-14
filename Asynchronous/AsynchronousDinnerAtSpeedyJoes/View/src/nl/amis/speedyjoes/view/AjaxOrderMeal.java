package nl.amis.speedyjoes.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Date;
import java.util.Iterator;

import javax.ejb.EJB;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import nl.amis.speedyjoes.business.MealOrderHandler;
import nl.amis.speedyjoes.common.Meal;
import nl.amis.speedyjoes.common.MealOrder;
import nl.amis.speedyjoes.common.log.ConversationLogger;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@WebServlet(name = "AjaxOrderMeal", urlPatterns = { "/ajaxordermeal" })
public class AjaxOrderMeal extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
    @EJB(name = "MealOrderHandlerAsynch")
    private MealOrderHandler mealOrderHandler;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>AjaxOrderMeal</title></head>");
        out.println("<body>");
        out.println("<p>The servlet has received a GET. This is the reply.</p>");
        out.println("</body></html>");
        out.close();

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Date startTime = new Date();
        String result = "post servlet";
        //String mealJSON = request.getParameter("meal");

        StringBuffer jb = new StringBuffer();
        String line = null;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null)
            jb.append(line);

        String mealJSON = jb.toString();
        System.out.println(mealJSON);

        String tableNumber = "";
        String drink = "";
        String main = "";
        String appetizer = "";

        JSONObject jObj;
        try {
            jObj = new JSONObject(mealJSON);
                tableNumber = (String) jObj.get("tableNumber");
                drink = (String) ((JSONObject) jObj.get("meal")).get("drink");
                main = (String) ((JSONObject) jObj.get("meal")).get("main");
                appetizer = (String) ((JSONObject) jObj.get("meal")).get("appetizer");
        } catch (JSONException e) {
            result = result + e.getMessage();
        }

        ConversationLogger conversationLogger = new ConversationLogger();

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

        conversationLogger.enterLog("Waiter", 1, "Taken drink to table ", 500);


        result = "so far so good;drink  " + meal.getDrink() + " and main " + meal.getMain();
        ;
        String json="{}";
        try {
            JSONObject r = new JSONObject().put("appetizerOrMain", "D");
            r.put("price", meal.getCheckTotal());
            r.put("menuItem", meal.getDrink());
            r.put("duration", (new Long((new Date().getTime() - startTime.getTime()))).intValue());
            JSONObject trace = conversationLogger.toJSON(); 
            r.put("trace",trace );
             json = r.toString();
            System.out.println("response=" + r);
        } catch (JSONException e) {
        }

        response.setContentType("application/json"); // Set content type of the response so that jQuery knows what it can expect.
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    private void sleep(int delayInMilliSeconds) {
        try {
            Thread.sleep((delayInMilliSeconds)); //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}
