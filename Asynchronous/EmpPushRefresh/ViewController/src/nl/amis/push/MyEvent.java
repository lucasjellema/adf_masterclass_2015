package nl.amis.push;

public class MyEvent {
    
    String payload;

    public MyEvent(String payload) {
        super();
        this.payload = payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
}
