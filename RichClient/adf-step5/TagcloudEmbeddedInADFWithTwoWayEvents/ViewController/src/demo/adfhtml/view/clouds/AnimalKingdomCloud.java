package demo.adfhtml.view.clouds;

import demo.adfhtml.view.Tag;

import java.util.ArrayList;
import java.util.List;

public class AnimalKingdomCloud {
    public AnimalKingdomCloud() {
        super();
    }
    
    public List<Tag> getTags() {
        List<Tag> tags = new ArrayList<Tag>();
        tags.add(new Tag("Elephant",15));
        tags.add(new Tag("Lion",35));
        tags.add(new Tag("Zebra",21));
        tags.add(new Tag("Rhinoceros",25));
        tags.add(new Tag("Tiger",12));
        tags.add(new Tag("Mammoth",3));
        tags.add(new Tag("Dog",39));
        tags.add(new Tag("Duck",13));
        tags.add(new Tag("Cat",42));
        return tags;
    }
}
