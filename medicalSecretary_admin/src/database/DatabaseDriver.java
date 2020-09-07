package database;

import base.Appointment;
import base.Doctor;
import base.Patient;
import base.Person;
import helper.Helper;
import javafx.stage.Stage;
import main.MedicalSecretary;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseDriver {
    public static Connection connection = null;
    //static String db = "src/database/inventory.db";
    protected static String dbClassName = "com.mysql.cj.jdbc.Driver";
    protected static String dbUrl = "jdbc:mysql://"+ MedicalSecretary.ip+":8889/medsec?serverTimezone=UTC&characterEncoding=utf-8";
    protected static String dbUser = "root";
    protected static String dbPwd = "root";

    public static void connection(){
        connection = null;
        try {
            Class.forName(dbClassName).newInstance();
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
            System.out.println("Opened database successfully");
        } catch ( Exception e ) {
            System.out.println( e.getClass().getName() + ": " + e.getMessage() );
            //System.exit(0);
        }
    }

    public static ArrayList<Appointment> getAppointmentByDate(String year, String month, String day){
        ArrayList<Appointment> appointments = new ArrayList<>();
        //DATE_FORMAT(a.date, '%Y-%m-%d')
        String sql = "SELECT a.id, a.uid, u.firstname, u.middlename, u.surname, a.did, d.name, a.title, a.date as appDate, "+
                "a.duration, a.status from Appointment a, User u, Doctor d where a.uid = u.id and a.did = d.id and Year(a.date) = ?";
        if (!month.equalsIgnoreCase("all")){
            sql += " and Month(a.date) = '" + month + "'";
        }
        if (!day.equalsIgnoreCase("all")){
            sql += " and Day(a.date) = '" + day + "'";
        }
        sql += " order by a.date desc";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,year);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String uid = resultSet.getString("uid");
                String firstname = resultSet.getString("firstname");
                String middlename = resultSet.getString("middlename");
                String surname = resultSet.getString("surname");
                String did = resultSet.getString("did");
                String doctorName = resultSet.getString("name");
                String title = resultSet.getString("title");
                String appDate = resultSet.getString("appDate");
                String duration = resultSet.getString("duration");
                String status = resultSet.getString("status");

                Appointment appointment = new Appointment(id,uid,firstname+" "+ middlename+" "+ surname, did,doctorName,title,appDate,duration,status);
                appointments.add(appointment);
            }
            preparedStatement.close();
            resultSet.close();
        }catch (SQLException e){
            Helper.displayHintWindow(null, "error", "Load data from database Failed!",
                    "Loading Appointment data from Database occurs a ERROR, the appointments cannot be loaded\nReason: "+ e.getMessage());
            e.printStackTrace();
        }

        return appointments;
    }

    public static void getAppointment(Appointment appointment) throws SQLException{
        String sql = "SELECT detail, date_create, date_change, note,user_note  "+
                " from Appointment where id = ?";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,appointment.getId());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                String detail = resultSet.getString("detail");
                String date_create = resultSet.getString("date_create");
                String date_change = resultSet.getString("date_change");
                String note = resultSet.getString("note");
                String user_note = resultSet.getString("user_note");

                appointment.setChangeDate(date_change);
                appointment.setCreateDate(date_create);
                appointment.setDetail(detail);
                appointment.setNote(note);
                appointment.setUserNote(user_note);
            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
    }

    public static void updateAppointment(Appointment appointment, String title, String detail, String dateTime,
                                       String duration, String status, String note, String userNote) throws SQLException{
        String sql = "Update Appointment a set a.title = ?, a.detail = ?, a.date = ?, a.duration = ?, a.status = ?, a.note = ?, a.user_note = ? "+
                "where a.id = ?";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,title);
            preparedStatement.setString(2,detail);
            preparedStatement.setString(3,dateTime);
            preparedStatement.setString(4,duration);
            preparedStatement.setString(5,status);
            preparedStatement.setString(6,note);
            preparedStatement.setString(7,userNote);
            preparedStatement.setString(8,appointment.getId());

            int i = preparedStatement.executeUpdate();
            if (i != 0){
                appointment.setTitle(title);
                appointment.setDetail(detail);
                appointment.setDateTime(dateTime);
                appointment.setDuration(duration);
                appointment.setStatus(status);
                appointment.setNote(note);
                appointment.setUserNote(userNote);
            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
    }

    public static void getAppointmentReport(Appointment appointment) throws SQLException{
        String sql = "SELECT id, title, link from File where apptid = ?  ";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,appointment.getId());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                String fileName = resultSet.getString("title");
                String id = resultSet.getString("id");
                String link = resultSet.getString("link");
                appointment.setFileID(id); appointment.setFileLink(link);
                appointment.setReport(fileName);
            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
    }

    public static void deleteAppoint(String id, String appID) throws SQLException{
        String sql = "delete from File where id = ? and apptid = ? ";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,id);
            preparedStatement.setString(2,appID);
            int i = preparedStatement.executeUpdate();
            if (i == 0){
                throw new SQLException("The file was not deleted.");
            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
    }
    public static void insertAppointment(Appointment appointment) throws SQLException{
        String sql = "insert into Appointment values (?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,appointment.getId());
            preparedStatement.setString(2,appointment.getUserID());
            preparedStatement.setString(3,appointment.getDoctorID());
            preparedStatement.setString(4,appointment.getTitle());
            preparedStatement.setString(5,appointment.getDetail());
            preparedStatement.setString(6,appointment.getCreateDate());
            preparedStatement.setString(7,appointment.getChangeDate());
            preparedStatement.setString(8,appointment.getDateTime());
            preparedStatement.setString(9,appointment.getDuration());
            preparedStatement.setString(10,appointment.getNote());
            preparedStatement.setString(11,appointment.getUserNote());
            preparedStatement.setString(12,appointment.getStatus());
            int i = preparedStatement.executeUpdate();
            if (i == 0){
                throw new SQLException("The file was not inserted.");
            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
    }

    //
    public static ArrayList<Person> getPersonForSelect(String type) throws SQLException{
        String sql = "SELECT id, firstname, middlename, surname, email from User where role = ?  ";
        if (type.equalsIgnoreCase("Doctor")){
            sql = "SELECT id, name, email from Doctor  ";
        }
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        ArrayList<Person> people = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (type.equalsIgnoreCase("patient")) preparedStatement.setString(1,"PATIENT");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = "";
                String email = resultSet.getString("email");
                if (type.equalsIgnoreCase("patient")) {
                    name = resultSet.getString("firstname") + " " + resultSet.getString("middlename")
                            + " "+ resultSet.getString("surname");
                    people.add(new Patient(id, name, email));
                }
                else {
                    name = resultSet.getString("name");
                    people.add(new Doctor(id, name, email));
                }


            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
        return people;
    }


    private static void closeDB(PreparedStatement ps, ResultSet rs) {
        try {
            if(ps != null) {
                ps.close();
            }
            if(rs != null){
                rs.close();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
