package base;

import java.util.ArrayList;

public class Patient extends Person{

    private String firstName, midName, surname;
    private String dob;
    private String street,suburb,state;
    private ArrayList<Resource> resources;
    public Patient(String id, String name, String email) {
        super(id, name, email);
    }
    public Patient(String id, String firstName, String midName, String surname, String dob, String email,
                   String street, String suburb, String state ) {
        super(id, firstName + " " + midName + " " + surname, email);
        this.firstName = firstName;
        this.midName = midName;
        this.surname = surname;
        this.dob = dob;
        this.street = street;
        this.suburb = suburb;
        this.state = state;
    }

    public ArrayList<Resource> getResources() {
        return resources;
    }

    public void setResources(ArrayList<Resource> resources){
        this.resources = resources;
    }
    private void addResource(Resource resource){
        resources.add(resource);
    }
    public String getFirstName() {
        return firstName;
    }

    public String getMidName() {
        return midName;
    }

    public String getSurname() {
        return surname;
    }

    public String getDob() {
        return dob;
    }

    public String getStreet() {
        return street;
    }

    public String getSuburb() {
        return suburb;
    }

    public String getState() {
        return state;
    }
}
