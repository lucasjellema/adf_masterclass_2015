package demo.adfhtml.view.clouds;

import demo.adfhtml.view.Tag;

import java.util.ArrayList;
import java.util.List;

public class CarsCloud {
    public CarsCloud() {
        super();
    }
    
    public List<Tag> getTags() {
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag("Mercedes",15));
        tags.add(new Tag("Ford",45));
        tags.add(new Tag("Chevrolet",11));
        tags.add(new Tag("Fiat",28));
        tags.add(new Tag("Peugeot",36));
        tags.add(new Tag("Renault",45));
        tags.add(new Tag("Toyota",37));
        tags.add(new Tag("Volkswagen",73));
        tags.add(new Tag("Seat",31));
        tags.add(new Tag("Jaguar",3));
        return tags;
    }
}
