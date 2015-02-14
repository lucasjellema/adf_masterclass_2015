package demo.adfhtml.view.zoo;

import demo.adfhtml.view.ADFHelper;


public class ZooTagClickedEventConsumer {
    
    public void handleEvent(Object payload) {
        ZooKeeper zk = (ZooKeeper)ADFHelper.evaluateEL("#{viewScope.zookeeper}");
        String selectedTag = (String)payload;
        zk.addAnimal(selectedTag);
    }
}
