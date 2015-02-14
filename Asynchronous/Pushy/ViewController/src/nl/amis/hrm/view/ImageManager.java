package nl.amis.hrm.view;
import java.util.ArrayList;
import java.util.List;

import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.render.ClientEvent;

public class ImageManager extends MessageManager{
    private String image;
    private List<String> imageList = new ArrayList<String>();
    
    
    public void setAlert(Object o) {
      System.out.println("setAlert "+o);
    }
    public ImageManager() {
      imageList.add("Chrysanthemum.jpg");
      imageList.add("Desert.jpg");
      imageList.add("Hydrangeas.jpg");
      imageList.add("Jellyfish.jpg");
      imageList.add("Koala.jpg");
      imageList.add("Lighthouse.jpg");
      imageList.add("Penguins.jpg");
      imageList.add("Tulips.jpg");
    }
    


    public void setImage(String image) {
        this.image= image;
        for (ActiveMessageBeanI amb: super.getListeners()) {
          amb.triggerDataUpdate(image);
        }
    }

    public String getImage() {
        return image;
    }
    
    public List<String> getImages() {
      return imageList;     
    }

  public void processImageSelection(ClientEvent clientEvent) {
    String image = (String)clientEvent.getParameters().get("imageName");
    System.out.println("image selected "+image);
    setImage(image);
  }
}