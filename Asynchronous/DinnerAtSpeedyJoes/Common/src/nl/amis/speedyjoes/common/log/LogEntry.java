package nl.amis.speedyjoes.common.log;

public class LogEntry {
    
    String component; int level; String description; int durationInMiliSeconds;


    long threadId = Thread.currentThread().getId();


    public long getThreadId() {
        return threadId;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public LogEntry(String component, int level, String description, int durationInMiliSeconds) {
        super();
        this.component = component;
        this.level = level;
        this.description = description;
        this.durationInMiliSeconds = durationInMiliSeconds;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getComponent() {
        return component;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDurationInMiliSeconds(int durationInMiliSeconds) {
        this.durationInMiliSeconds = durationInMiliSeconds;
    }

    public int getDurationInMiliSeconds() {
        return durationInMiliSeconds;
    }
}
