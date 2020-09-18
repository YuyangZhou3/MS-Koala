package base;

public class Hospital {
    private String id;
    private String name;
    private String address;
    private String emergencyDept;
    private String phone;
    private String aftPhone;
    private String fax;
    private String email;
    private String website;

    public Hospital(String id, String name, String address, String emergencyDept, String phone, String aftPhone, String fax, String email, String website) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.emergencyDept = emergencyDept;
        this.phone = phone;
        this.aftPhone = aftPhone;
        this.fax = fax;
        this.email = email;
        this.website = website;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyDept() {
        return emergencyDept;
    }

    public void setEmergencyDept(String emergencyDept) {
        this.emergencyDept = emergencyDept;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAftPhone() {
        return aftPhone;
    }

    public void setAftPhone(String aftPhone) {
        this.aftPhone = aftPhone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
