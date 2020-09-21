package base;

public class Radiology {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String fax, email;
    private String hours;
    private String website;

    public Radiology(String id, String name, String address, String phone, String fax, String hours, String email, String website) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.hours = hours;
        this.fax =fax;
        this.email =email;
        this.website = website;
    }

    public String getFax() {
        return fax;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getHours() {
        return hours;
    }

    public String getWebsite() {
        return website;
    }
}
