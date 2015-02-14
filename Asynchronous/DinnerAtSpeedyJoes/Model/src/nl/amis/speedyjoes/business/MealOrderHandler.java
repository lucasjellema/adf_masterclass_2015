package nl.amis.speedyjoes.business;

import javax.ejb.Remote;

import nl.amis.speedyjoes.common.Meal;
import nl.amis.speedyjoes.common.MealOrder;
import nl.amis.speedyjoes.common.log.ConversationLogger;

@Remote
public interface MealOrderHandler {
    
    public void handleMealOrder(Meal meal, ConversationLogger conversationLogger, int level);
}
