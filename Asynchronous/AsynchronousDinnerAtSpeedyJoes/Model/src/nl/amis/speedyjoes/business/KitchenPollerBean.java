package nl.amis.speedyjoes.business;

import com.sun.corba.se.impl.oa.poa.AOMEntry;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;

import javax.ejb.Local;
import javax.ejb.Schedule;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TimedObject;
import javax.ejb.Timer;

import javax.inject.Inject;

import javax.jms.ConnectionFactory;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import javax.jms.TextMessage;

import javax.naming.Context;

import javax.naming.InitialContext;

import javax.naming.NamingException;

import javax.sql.DataSource;

import nl.amis.speedyjoes.common.Meal;
import nl.amis.speedyjoes.common.MealOrder;
import nl.amis.speedyjoes.common.log.ConversationLogger;

import weblogic.jms.client.JMSContext;

@Stateless(name = "KitchenPoller", mappedName = "DinnerAtSpeedyJoesAsynchronous-Model-KitchenPoller")
@Local
public class KitchenPollerBean implements TimedObject {

    private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
    @Resource
    SessionContext sessionContext;


    //    @Resource(lookup = "jms/JoesConnectionFactory")
    //    ConnectionFactory cf;
    //
    //    @Resource(lookup = "jms/JoesQueue")
    //    Queue joesQueue;


    @Resource(lookup = "jdbc/joesRestaurant")
    private DataSource dataSource;

    public KitchenPollerBean() {
    }

    public void ejbTimeout(Timer timer) {
    }

    @Schedule(second = "*/5", minute = "*", hour = "*", info = "KitchenPollSchedule")
    public void pollKitchenForReadyMealItems(Timer t) {
        fetchReadyMealItemsFromKitchen();
    }


    private void fetchReadyMealItemsFromKitchen() {
        String result = "init";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            // fetch the prepared dishes
            PreparedStatement findReadyUndeliveredMealItemsStatement =
                conn.prepareStatement("SELECT menu_item, extract( second from (end_time - start_time)) as preparation_duration" +
                                      ", price , upper(APPETIZER_OR_MAIN) as aOrM , table_Number " +
                                      "from kitchen_orders where retrieved_yn='N' and end_time is not null");

            String updateRetrievedReadyDishesSQL =
                "UPDATE kitchen_orders set retrieved_yn = 'Y' where table_number = ? and menu_item = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(updateRetrievedReadyDishesSQL);


            ResultSet set = findReadyUndeliveredMealItemsStatement.executeQuery();
            result = "queried";
            ConversationLogger conversationLogger = new ConversationLogger();

            while (set.next()) {

                result = "query result found";
                String menuItem = set.getString("menu_item");
                String tableNumber = set.getString("table_number");
                String appetizerOrMain = set.getString("aOrM");
                Object price = set.getObject("price");
                Object duration = set.getObject("preparation_duration");

                conversationLogger.enterLog("Kitchen", 1, "Cooked Meal Item", Math.round(1000*((BigDecimal)duration).floatValue()),-1);
                logger.log(Level.SEVERE, "Found meal item " + menuItem + " to deliver to table " + tableNumber);

                preparedStatement.setString(1, tableNumber);
                preparedStatement.setString(2, menuItem);
                // take prepared item from kitchen by updating the retrieved_yn value
                preparedStatement.executeUpdate();
                logger.log(Level.SEVERE, "Fetched meal item and updated to delivered");

                sleep(200);
                conversationLogger.enterLog("Mid Office", 2, "Fetched Meal from Kitchen", 200);

                publishMealItemToJMS(menuItem, tableNumber, appetizerOrMain, ((BigDecimal)price).floatValue()
                                     , Math.round(1000*((BigDecimal)duration).floatValue()), conversationLogger,3);

            } //while
            result = "done set processing";
        }


        catch (SQLException ex) {
            //EXCEPTION HANDLING
            result = ex.getMessage();
        } catch (Exception ex) {
            //EXCEPTION HANDLING
            result = "general exception " + result + " :" + ex.getMessage();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                result = ex.getMessage();
            }
        }
        result = "before final enterlog " + result;
        logger.log(Level.SEVERE, result);

    }


    private QueueConnectionFactory qconFactory;
    private QueueConnection qcon;
    private QueueSession qsession;
    private QueueSender qsender;
    private Queue queue;
    private TextMessage msg;


    private void publishMealItemToJMS(String menuItem, String tableNumber, String aOrM, float price, int duration
                                      , ConversationLogger conversationLogger, int level) throws javax.jms.JMSException {
        //JMS publication
        try {

            Context jndiContext = null;
            try {
                jndiContext = new InitialContext();
            } catch (NamingException e) {
                System.out.println("Could not create JNDI API " + "context: " + e.toString());
                System.exit(1);
            }

            /*
            * Look up connection factory and destination. If either
            * does not exist, exit. If you look up a
            * TopicConnectionFactory or a QueueConnectionFactory,

            * program behavior is the same.
            */
            QueueConnectionFactory connectionFactory = null;
            Destination dest = null;
            try {
                connectionFactory = (QueueConnectionFactory) jndiContext.lookup("jms/JoesConnectionFactory");
                dest = (Destination) jndiContext.lookup("jms/JoesQueue");

            } catch (Exception e) {
                System.out.println("JNDI API lookup failed: " + e.toString());
                e.printStackTrace();

            }
            
            String message = "Found meal item " + menuItem + " to deliver to table " + tableNumber;
                        //       javax.jms.Connection connection = connectionFactory.createConnection();
            qcon = connectionFactory.createQueueConnection();
            qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            //qsender = qsession.createSender((javax.jms.Queue) dest);
            qcon.start();          
            MapMessage mm= qsession.createMapMessage();
            mm.setString("tableNumber", tableNumber);
            mm.setString("menuItem", menuItem);
            mm.setString("AorM", aOrM);
            mm.setInt("duration", duration);
            mm.setFloat("price", price);
            mm.setString("trace", conversationLogger.toJSON().toString());
            MessageProducer producer = qsession.createProducer(dest);
            producer.send(mm);
            sleep(900);
            conversationLogger.enterLog("Mid Office", level, "Handed Cooked Meal Item Off to Waiters Station",
                                        900);
        } finally {
            if (qcon != null) {
                try {
                    qcon.close();
                } catch (JMSException e) {
                }
            }
        }
    }

    private void sleep(int delayInMilliSeconds) {
        try {
            Thread.sleep((delayInMilliSeconds)); //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}
