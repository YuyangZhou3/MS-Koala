package com.medsec.util;

import com.medsec.entity.*;
import org.json.simple.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

/**
 * DataManager that process JSON and return entity
 */
public class DataManager {

    private static com.medsec.util.DataManager instance = null;

    public static com.medsec.util.DataManager getInstance() {
        if (instance == null) {
            instance = new com.medsec.util.DataManager();
        }
        return instance;
    }

    /** create patient instance from json object */
    public User processPatient(JSONObject user) {
        String id = (String) user.get("Id");
        String firstName = (String) user.get("FirstName");
        String middleName = (String) user.get("MiddleName");
        String surname = (String) user.get("Surname");
        String dob = dobConvert((String) user.get("DOB"));
        String email = (String) user.get("EmailAddress");
        String addrLine1 = (String) user.get("AddressLine1");
        String addrLine2 = (String) user.get("AddressLine2");
        String street = null;
        if (addrLine2 != null){
            street = addrLine1 + ", " + addrLine2;
        }else{
            street = addrLine1;
        }
        String suburb = (String) user.get("Suburb");
        String state = (String) user.get("State");
        User patient = new User().id(id).firstname(firstName).middlename(middleName).surname(surname).dob(dob)
                .email(email).street(street).suburb(suburb).state(state).role(UserRole.PATIENT);
        return patient;
    }

    public String dobConvert(String dob) {
        String[] dobArray = dob.split("/");
        String day = dobArray[0];
        String month = dobArray[1];
        String year = dobArray[2];
        if (Integer.parseInt(day) < 10){
            day = "0" + day;
        }
        String dobNew = year + "-" + month + "-" + day;
        return dobNew;
    }

    /** create appointment instance from json object */
    public Appointment processAppt(JSONObject appt) {
        String id = (String) appt.get("Id");
        String uid = (String) appt.get("PT_Id_Fk");
        String did = (String) appt.get("ProviderID");
        String title = (String) appt.get("Name");
        String detail = (String) appt.get("Reason");
        // CreationDate time is in UTC time zone
        Instant dateCreate = dateConvert((String) appt.get("CreationDate"));
        // StartTime is local time of clinic
        long startTime = startTimeConvert((String) appt.get("StartTime"));
        // LastUpdated time is in UTC time zone
        Instant dateChange = updateTimeConvert((String) appt.get("LastUpdated"));
        Instant dateStart = dateConvert((String) appt.get("StartDate"));
        Instant date = startDateTimeConvert(dateStart, startTime);
        int duration = Integer.parseInt((String) appt.get("ApptDuration"))/60;
        String note = (String) appt.get("Note");
        Appointment appointment = new Appointment().id(id).uid(uid).did(did).title(title).detail(detail)
                .date_create(dateCreate).date_change(dateChange).date(date).duration(duration).note(note).status(AppointmentStatus.UNCONFIRMED);
        return appointment;
    }

    /** get the correct date format from String */
    public Instant dateConvert(String date) {
        String[] dateArray = date.split("/");
        String day = dateArray[0];
        String month = dateArray[1];
        String year = dateArray[2];
        if (Integer.parseInt(day) < 10){
            day = "0" + day;
        }
        String time = year + "-" + month + "-" + day + "T" + "00" + ":" + "00" + ":" + "00"
                + ".000Z";
        Instant instant = Instant.parse(time);
        return instant;
    }

    /* get the correct start time of an appointment */
    public long startTimeConvert(String startTimeStr) {

        String[] timeArray = startTimeStr.split(":");
        long hour = Long.parseLong(timeArray[0]);
        long minute = Long.parseLong(timeArray[1]);
        long second = Long.parseLong(timeArray[2]);
        long startTime = hour*3600 + minute*60 +second;

        return startTime;
    }

    /* get the correct start date time of an appointment */
    public Instant startDateTimeConvert(Instant startDate, long startTime) {
//        startDate = startDate.minus(Duration.ofHours(10));
        Instant startDateTime = startDate.plusSeconds(startTime);
        return startDateTime;
    }

    /** get the correct update time from String */
    public Instant updateTimeConvert(String lastChangeDate) {
        String year = lastChangeDate.substring(0, 4);
        String month = lastChangeDate.substring(4, 6);
        String day = lastChangeDate.substring(6, 8);
        String hour = lastChangeDate.substring(8, 10);
        String minute = lastChangeDate.substring(10, 12);
        String second = lastChangeDate.substring(12);
        String updateTime = year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second
                + ".000Z";
        Instant instant = Instant.parse(updateTime);
        return instant;
    }

    /** create doctor instance from json object */
    public Doctor processDoctor(JSONObject dctor) {
        String id = (String) dctor.get("Id");
        String name = (String) dctor.get("Name");
        String bio = (String) dctor.get("Bio");
        String address = (String) dctor.get("Address");
        String phone = (String) dctor.get("Phone");
        String fax = (String) dctor.get("Fax");
        String email = (String) dctor.get("Email");
        String website = (String) dctor.get("Website");
        String expertise = (String) dctor.get("Expertise");
        Doctor doctor = new Doctor().id(id).name(name).bio(bio).address(address).phone(phone)
                .fax(fax).email(email).website(website).expertise(expertise);
        return doctor;
    }

    /** create hospital instance from json object */
    public Hospital processHospital(JSONObject hspital) {
        String id = (String) hspital.get("Id");
        String name = (String) hspital.get("Name");
        String address = (String) hspital.get("Address");
        String emergencyDept = (String) hspital.get("EmergencyDept");
        String phone = (String) hspital.get("Phone");
        String aftPhone = (String) hspital.get("AftPhone");
        String fax = (String) hspital.get("Fax");
        String email = (String) hspital.get("Email");
        String website = (String) hspital.get("Website");
        Hospital hospital = new Hospital().id(id).name(name).address(address).emergencyDept(emergencyDept)
                .phone(phone).aftPhone(aftPhone).fax(fax).email(email).website(website);
        return hospital;
    }

    /** create pathology instance from json object */
    public Pathology processPathology(JSONObject pthology) {
        String id = (String) pthology.get("Id");
        String name = (String) pthology.get("Name");
        String address = (String) pthology.get("Address");
        String phone = (String) pthology.get("Phone");
        String hours = (String) pthology.get("Hours");
        if (hours != null){ hours = hours.replaceAll("\u2013", "-"); }
        String website = (String) pthology.get("Website");
        Pathology pathology = new Pathology().id(id).name(name).address(address).phone(phone)
                .hours(hours).website(website);
        return pathology;
    }

    /** create radiology instance from json object */
    public Radiology processRadiology(JSONObject rdiology) {
        String id = (String) rdiology.get("Id");
        String name = (String) rdiology.get("Name");
        String address = (String) rdiology.get("Address");
        String phone = (String) rdiology.get("Phone");
        String fax = (String) rdiology.get("Fax");
        String hours = (String) rdiology.get("Hours");
        String email = (String) rdiology.get("Email");
        String website = (String) rdiology.get("Website");
        Radiology radiology = new Radiology().id(id).name(name).address(address).phone(phone)
                .fax(fax).hours(hours).email(email).website(website);
        return radiology;
    }

    /** create resource instance from json object */
    public Resource processResource(JSONObject rsource) {
        String id = (String) rsource.get("Id");
        String uid = (String) rsource.get("Uid");
        String name = (String) rsource.get("Name");
        String res = (String) rsource.get("Resource");
        Resource resource = new Resource().id(id).uid(uid).name(name).resource(res);
        return resource;
    }
}
