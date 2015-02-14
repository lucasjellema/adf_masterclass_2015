package nl.amis.hrm.view;

public interface ActiveMessageBeanI {

    public void triggerDataUpdate(String message);

    public void setMsgMgr(MessageManager msgMgr);

    public MessageManager getMsgMgr();
}
