package demo.adfhtml.view;

public class MatchEvent {
    
    private String eventType;  //time, goal, half time, full time, wins on penalties, red card
    private String timeLabel; // First Half, Half Time Break, Second Half, Full Time, Extra Time, Penalty Shoot Out
    private String homeOrAway; //home / away
    private Boolean home; 
    private Boolean away; 

    public MatchEvent(String eventType, String timeLabel, String homeOrAway) {
        super();
        this.eventType = eventType;
        this.timeLabel = timeLabel;
        this.homeOrAway = homeOrAway;
        this.home = ("home".equalsIgnoreCase(homeOrAway));
        this.away = ("away".equalsIgnoreCase(homeOrAway));
    } 


    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
    }

    public String getTimeLabel() {
        return timeLabel;
    }


    public Boolean getHome() {
        return home;
    }
    public Boolean getAway() {
        return away;
    }

}
