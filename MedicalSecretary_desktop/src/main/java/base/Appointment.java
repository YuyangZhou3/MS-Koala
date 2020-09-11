package base;

import helper.Helper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Appointment {
    private String id;
    private String userName;
    private String userID;
    private String doctorName;
    private String doctorID;
    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty dateTime = new SimpleStringProperty("");
    private final StringProperty duration = new SimpleStringProperty("");
    private final StringProperty status = new SimpleStringProperty("UNCONFIRMED");
    private String detail;
    private String note;
    private String userNote;
    private final StringProperty report = new SimpleStringProperty("None");
    private String createDate;
    private String changeDate;
    private String date, time;
    private String fileID, fileLink;

    public Appointment(){
        this(null, null, null, null, null, "", "","","");
    }
    public Appointment(String id,  String userID,String userName, String doctorID, String doctorName,String title, String dateTime, String duration, String status){
        this.id = id;
        this.userID = userID;
        this.userName = userName;
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.title.set(title);
        this.dateTime.set(dateTime);
        this.status.set(status);
        this.duration.set(duration);

        createDate = null;
        changeDate = null;
        userNote = null;
        note = null;
        detail = null;
        fileID = null;
        fileLink = null;
        setDateAndTime();
    }
    public String getUserNameID(){
        return userName + "(" + userID + ")";
    }

    public String getDoctorNameID(){
        return doctorName + "(" + doctorID + ")";
    }

    public void setDateAndTime(){
        String[] datetimes = dateTime.get().split(" ");
        if (datetimes.length == 2 ){
            date = datetimes[0];
            time = datetimes[1];
        }else {
            date = null;
            time = null;
        }
    }

    public void createNew(String title, String detail, LocalDate date, String time, String duration,
                          String status, String note, String userNote) throws Exception{
        if (userID == null){
            throw new Exception("Patient did not selected.");
        }
        if (doctorID == null){
            throw new Exception("Doctor did not selected");
        }
        if (title == null || title.isEmpty()){
            throw new Exception("Title must not be empty.");
        }
        this.title.set(title);
        this.detail = detail;
        if (date == null){
            throw new Exception("Appointment date must not be empty.");
        }
        if (time == null || time.isEmpty()){
            throw new Exception("Time must not be empty.");
        }
        dateTime.set(date.format(Helper.dtf) + " " + time);
        if (duration == null || duration.isEmpty()){
            throw new Exception("duration must not be empty.");
        }
        this.status.set(status);
        this.note = note;
        this.userNote = userNote;
        try {
            int dur = Integer.parseInt(duration);
            this.duration.set(dur+"");
        }catch (Exception e){
            throw new Exception("duration must not be Integer.");
        }
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getFileLink() {
        return fileLink;
    }

    public void setFileLink(String fileLink) {
        this.fileLink = fileLink;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDateTime() {
        return dateTime.get();
    }

    public StringProperty dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime.set(dateTime);
        setDateAndTime();
    }

    public String getDuration() {
        return duration.get();
    }

    public StringProperty durationProperty() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUserNote() {
        return userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }

    public String getReport() {
        return report.get();
    }

    public StringProperty reportProperty() {
        return report;
    }

    public void setReport(String report) {
        this.report.set(report);
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
