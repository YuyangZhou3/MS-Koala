package base;

import database.DatabaseDriver;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.SQLException;

public class Doctor extends Person {

   private String bio;
   private String address;
   private final StringProperty phone = new SimpleStringProperty("");
   private String fax;
   private String website;
    private final StringProperty expertise = new SimpleStringProperty("");

    public Doctor(String id, String name, String email) {
        this(id, name, email, null, null, null, null, null, null);
    }
    public Doctor(String id, String name, String email,String bio, String phone, String address,
                  String fax, String website, String expertise) {
        super(id, name, email);
        this.bio = bio;
        this.phone.set(phone);
        this.address = address;
        this.fax = fax;
        this.website = website;
        this.expertise.set(expertise);
    }
    public void updateDataToDatabase(String name, String email,String bio, String phone, String address,
                                     String fax, String website, String expertise) throws Exception {
        if (name == null || name.trim().isEmpty()){
            throw new Exception("The Doctor Name must be not empty");
        }
        DatabaseDriver.updateDoctor(this, name, email, bio, phone, address, fax, website, expertise);
    }
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getExpertise() {
        return expertise.get();
    }

    public StringProperty expertiseProperty() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise.set(expertise);
    }
}
