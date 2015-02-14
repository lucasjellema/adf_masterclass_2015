package deck.view.bean;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.layout.RichDeck;

import org.apache.myfaces.trinidad.context.RequestContext;

public class DynamicDeckBean {
    public DynamicDeckBean() {
    }
    private RichDeck deckBind;
    
    private String transitionEffect="flipLeft";

    public void setTransitionEffect(String transitionEffect) {
        this.transitionEffect = transitionEffect;
    }

    public String getTransitionEffect() {
        return transitionEffect;
    }

    public void setDeckBind(RichDeck deckBind) {
        this.deckBind = deckBind;
    }

    public RichDeck getDeckBind() {
        return deckBind;
    }
/**Methods to be called on different links to show different images*/
    public void link1Action(ActionEvent actionEvent) {
        UIComponent eventComponent = deckBind;
        _animateDeckDisplayedChild(eventComponent, 0);// 0 for first child of deck
    }
    public void link2Action(ActionEvent actionEvent) {
        UIComponent eventComponent = deckBind;
        _animateDeckDisplayedChild(eventComponent, 1);// 1 for second child of deck
    }
    public void link3Action(ActionEvent actionEvent) {
        UIComponent eventComponent = deckBind;
        _animateDeckDisplayedChild(eventComponent, 2);
    }
    public void link4Action(ActionEvent actionEvent) {
        UIComponent eventComponent = deckBind;
        _animateDeckDisplayedChild(eventComponent, 3);
    }
    public void link5Action(ActionEvent actionEvent) {
        UIComponent eventComponent = deckBind;
        _animateDeckDisplayedChild(eventComponent, 4);
    }
    public void link6Action(ActionEvent actionEvent) {
        UIComponent eventComponent = deckBind;
        _animateDeckDisplayedChild(eventComponent, 5);
    }
    // Animate the display of a deck child.
    private void _animateDeckDisplayedChild(UIComponent eventComponent, int newDisplayedChildIndex) {
        // Find the nearest deck ancestor:
        RichDeck deck = null;
        String eventComponentId = eventComponent.getId();
        while (deck == null) {
            if (eventComponent == null) {
                System.err.println("Unable to locate a deck ancestor from id " + eventComponentId);
                return;
            } else if (eventComponent instanceof RichDeck) {
                deck = (RichDeck) eventComponent;
                break;
            }
            eventComponent = eventComponent.getParent();
        }
        System.out.println("Child is-" + eventComponent.getId());
        String newDisplayedChild = deck.getChildren().get(newDisplayedChildIndex).getId();

        // Update the displayedChild:
        System.out.println("Display Child-" + newDisplayedChild);
        deck.setDisplayedChild(newDisplayedChild);

        // Add this component as a partial target:
        RequestContext.getCurrentInstance().addPartialTarget(deck);
    }

}
