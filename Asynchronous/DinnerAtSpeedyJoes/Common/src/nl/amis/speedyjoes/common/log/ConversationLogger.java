package nl.amis.speedyjoes.common.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ConversationLogger {
    
    List<LogEntry> logEntries = new ArrayList<LogEntry>();
    public ConversationLogger() {
        super();
    }
    public ConversationLogger(String json) {        
        super();
        
        JSONObject jObj;
        try {
            jObj = new JSONObject(json);
            //
            JSONArray entries = jObj.getJSONArray("logEntries");
            for (int i = 0 ; i < entries.length(); i++) {
                JSONObject obj = entries.getJSONObject(i);
                enterLog(obj.getString("component"), obj.getInt("level"),obj.getString("description"), obj.getInt("duration"), obj.getLong("thread"));
            }//for
        } catch (JSONException e) {
            System.out.println("Constructor for Conversation Logger "+e.getMessage());
        }

    }
        
        
    private long startTime;
    public void start() {
        startTime = new Date().getTime();
    }
    public int stop() {
        return (new Long((new Date().getTime() - startTime))).intValue();
    }
    
    public void enterLog(String component, int level, String description, int durationInMiliSeconds) {
        logEntries.add(new LogEntry(component, level, description, durationInMiliSeconds));
    }
    
    public int getHighestLevel() {
        int highestLevel = 0;
        for (LogEntry entry:this.getLogEntries()){
           if (entry.getLevel()> highestLevel)
            highestLevel = entry.getLevel();
        }//for        
        return highestLevel;
    }
    
    public void enterLog(String component, int level, String description, int durationInMiliSeconds, long thread) {
        LogEntry entry = new LogEntry(component, level, description, durationInMiliSeconds);
        entry.setThreadId(thread);
        logEntries.add(entry);
    }

    public void setLogEntries(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    public List<LogEntry> getLogEntries() {
        return logEntries;
    }
    
    public JSONObject toJSON() {        
      JSONObject r =null;
      try {
        JSONArray logEntries = new JSONArray();
        for (LogEntry entry:this.getLogEntries()){
            JSONObject logEntry = new JSONObject();
            logEntry.put("component", entry.getComponent());
            logEntry.put("description", entry.getDescription());
            logEntry.put("duration", entry.getDurationInMiliSeconds());
            logEntry.put("thread", entry.getThreadId());
            logEntry.put("level", entry.getLevel());
            logEntries.put(logEntry);            
        }//for        
        r = new JSONObject().put("logEntries", logEntries); 
       
    } catch (JSONException e) {
        System.out.println("toJSON exception "+e.getMessage());
    }
    return r;
  }//toJSON
}