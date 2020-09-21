package database;

import base.*;
import helper.Helper;
import app.MedicalSecretary;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseDriver {
    public static Connection connection = null;
    protected static String dbClassName = "com.mysql.cj.jdbc.Driver";
    protected static String dbUrl = "jdbc:mysql://"+ MedicalSecretary.ip+":8889/medsec?serverTimezone=UTC&characterEncoding=utf-8";
    protected static String dbUser = "root";
    protected static String dbPwd = "root";

    public static void connection(){
        connection = null;
        try {
            Class.forName(dbClassName);
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
            System.out.println("Opened database successfully");
        } catch ( Exception e ) {
            System.out.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(1);
        }
    }

    public static ArrayList<Appointment> getAppointmentByDate(String year, String month, String day){
        ArrayList<Appointment> appointments = new ArrayList<>();
        //DATE_FORMAT(a.date, '%Y-%m-%d')
        String sql = "SELECT a.id, a.uid, u.firstname, u.middlename, u.surname, a.did, d.name, a.title,a.detail,a.date_create,a.date_change, a.note,a.user_note, a.date as appDate, "+
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
                String detail = resultSet.getString("detail");

                String date_create = resultSet.getString("date_create");
                String date_change = resultSet.getString("date_change");
                String note = resultSet.getString("note");
                String user_note = resultSet.getString("user_note");

                Appointment appointment = new Appointment(id,uid,firstname+" "+ middlename+" "+ surname, did,doctorName,title,appDate,duration,status);
                appointment.setChangeDate(date_change);
                appointment.setCreateDate(date_create);
                appointment.setDetail(detail);
                appointment.setNote(note);
                appointment.setUserNote(user_note);
                getAppointmentReport(appointment);
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

    public static ArrayList<Doctor> getDoctors() throws SQLException{
        ArrayList<Doctor> doctors = new ArrayList<>();
        var sql = "SELECT id,name,bio,address,phone,fax,email,website,expertise FROM Doctor";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String bio = resultSet.getString("bio");
                String address = resultSet.getString("address");
                String phone = resultSet.getString("phone");
                String fax = resultSet.getString("fax");
                String email = resultSet.getString("email");
                String website = resultSet.getString("website");
                String expertise = resultSet.getString("expertise");

                doctors.add(new Doctor(id,name,email, bio,phone,address,fax,website,expertise));
            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
        return doctors;
    }

    public static void updateDoctor(Doctor doctor, String name, String email,String bio, String phone, String address,
                                    String fax, String website, String expertise) throws SQLException{
        var sql = "Update Doctor set name = ?, email = ?, bio = ?, phone = ?, address = ?, fax = ?, website = ?, expertise = ?  "+
                "where id = ?";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,bio);
            preparedStatement.setString(4,phone);
            preparedStatement.setString(5,address);
            preparedStatement.setString(6,fax);
            preparedStatement.setString(7,website);
            preparedStatement.setString(8,expertise);
            preparedStatement.setString(9,doctor.getId());

            int i = preparedStatement.executeUpdate();
            if (i != 0){
                doctor.setName(name);
                doctor.setEmail(email);
                doctor.setBio(bio);
                doctor.setPhone(phone);
                doctor.setAddress(address);
                doctor.setFax(fax);
                doctor.setWebsite(website);
                doctor.setExpertise(expertise);
            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
    }
    public static void deleteData(String type, String id) throws SQLException{
        String sql = "delete from "+type+" where id = ? ";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,id);
            int i = preparedStatement.executeUpdate();
            if (i == 0){
                throw new SQLException("The "+type+" was not deleted.");
            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
    }

    public static ArrayList<Hospital> getHospitals() throws SQLException{
        ArrayList<Hospital> hospitals = new ArrayList<>();
        var sql = "SELECT id,name,address,emergencyDept,phone,aftPhone,fax,email,website FROM Hospital";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String emergencyDept = resultSet.getString("emergencyDept");
                String address = resultSet.getString("address");
                String phone = resultSet.getString("phone");
                String fax = resultSet.getString("fax");
                String email = resultSet.getString("email");
                String website = resultSet.getString("website");
                String aftPhone = resultSet.getString("aftPhone");

                hospitals.add(new Hospital(id,name,address, emergencyDept,phone,aftPhone,fax,email,website));
            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
        return hospitals;
    }
    public static ArrayList<Pathology> getPathologies() throws SQLException{
        ArrayList<Pathology> pathologies = new ArrayList<>();
        var sql = "SELECT id,name,address,phone,hours,website FROM Pathology";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String phone = resultSet.getString("phone");
                String hours = resultSet.getString("hours");
                String website = resultSet.getString("website");

                pathologies.add(new Pathology(id,name,address,phone,hours,website));
            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
        return pathologies;
    }
    public static ArrayList<Radiology> getRadiologies() throws SQLException{
        ArrayList<Radiology> radiologies = new ArrayList<>();
        var sql = "SELECT id,name,address,phone,fax,hours,email,website FROM Radiology";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String phone = resultSet.getString("phone");
                String hours = resultSet.getString("hours");
                String website = resultSet.getString("website");
                String fax = resultSet.getString("fax");
                String email = resultSet.getString("email");

                radiologies.add(new Radiology(id,name,address,phone,fax,hours,email,website));
            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
        return radiologies;
    }

    public static ArrayList<Patient> getPatients() throws SQLException{
        ArrayList<Patient> patients = new ArrayList<>();
        var sql = "SELECT id,firstname,middlename,surname,dob,email,street,suburb,state FROM User where role = 'PATIENT'";
        PreparedStatement preparedStatement =null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String id = resultSet.getString("id");
                String firstname = resultSet.getString("firstname");
                String middlename = resultSet.getString("middlename");
                String surname = resultSet.getString("surname");
                String dob = resultSet.getString("dob");
                String email = resultSet.getString("email");
                String street = resultSet.getString("street");
                String suburb = resultSet.getString("suburb");
                String state = resultSet.getString("state");

                patients.add(new Patient(id,firstname,middlename,surname,dob,email,street,suburb,state));
            }
        }finally {
            closeDB(preparedStatement, resultSet);
        }
        return patients;
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
