package com.medsec.util;

import com.medsec.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.*;
import javax.servlet.ServletContext;
import java.io.*;
import java.io.File;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

/**
 * TCPServer handles JSON GENIE Data sent from the script
 */
public class TCPServer implements Runnable{

    private static final Logger log = LogManager.getLogger();
    private static final String CLIENT_KEY_STORE_PASSWORD = "client";
    private static final String CLIENT_TRUST_KEY_STORE_PASSWORD = "client";
    private static final String CLIENT_KEY_PATH = "/client_ks.jks";
    private static final String TRUST_SERVER_KEY_PATH = "/serverTrust_ks.jks";

    public static String GENIE_INSTALL_PATH = "";

    SSLServerSocket serverSocket;

    public TCPServer(SSLServerSocket s){
            this.serverSocket = s;
    }

    public void run()
    {
        System.out.println("Threaded Server Running");
        while(true)
        {
            try {
            Socket sock = serverSocket.accept();
            TCPServerProcess s = new TCPServerProcess(sock);
            Thread serverThread = new Thread(s);
            serverThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void initialSSLServerSocket(int PORT) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(TCPServer.class.getResourceAsStream(CLIENT_KEY_PATH), CLIENT_KEY_STORE_PASSWORD.toCharArray());
            KeyStore tks = KeyStore.getInstance("JKS");
            tks.load(TCPServer.class.getResourceAsStream(TRUST_SERVER_KEY_PATH), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());


            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(tks);

            SSLContext context = SSLContext.getInstance("SSL");

            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            SSLServerSocketFactory ssf = context.getServerSocketFactory();
//            SSLServerSocket serverSocket = initialSSLServerSocket(PORT);
            SSLServerSocket s = (SSLServerSocket) ssf.createServerSocket(PORT);
            TCPServer socketServer = new TCPServer(s);
            Thread serverThread = new Thread(socketServer);
            serverThread.start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}

class TCPServerProcess implements Runnable{
    private static final Logger log = LogManager.getLogger();
    private volatile boolean flag = false;
    Socket connectionSocket;
    DataManager dataManager = DataManager.getInstance();
    ServletContext sc = null;


    public TCPServerProcess(Socket s){
        try{
            System.out.println("Client Connected");
            connectionSocket = s;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void run(){
        try{
            // BufferedReader can handle arbitrary length string
            String msg;
            InputStream in = connectionSocket.getInputStream();

            DataInputStream dataInputStream = new DataInputStream(in);
            try {

                while(!flag && (msg = dataInputStream.readUTF()) != null) {
                    flag = processData(msg);
                }
                Thread.currentThread().interrupt();
                if (Thread.currentThread().isInterrupted())
                {
                    log.info(Thread.currentThread().getName() +" is closed");
                }
            } catch (SocketException e) {
                System.out.println("closed connection");
            }
            connectionSocket.close();
        }catch(Exception e){
            e.printStackTrace();}
    }

    public boolean processData(String data) throws IOException {
        JSONObject json = JSONReader.getInstance().parse(data);
        String command = (String) json.get("command");

        if (command.equals(QueryCommand.AUTHENTICATION.toString())){
            String secret = (String) json.get("secret");
            return authenticationHandler(secret);
        }
        else if (command.equals(QueryCommand.PATIENT.toString()))
            return userHandler((JSONObject) json.get("doc"));
        else if (command.equals(QueryCommand.APPOINTMENT.toString()))
            return apptHandler((JSONObject) json.get("doc"));
        else if (command.equals(QueryCommand.DOCTOR.toString()))
            return doctorHandler((JSONObject) json.get("doc"));
        else if (command.equals(QueryCommand.HOSPITAL.toString()))
            return hospitalHandler((JSONObject) json.get("doc"));
        else if (command.equals(QueryCommand.PATHOLOGY.toString()))
            return pathologyHandler((JSONObject) json.get("doc"));
        else if (command.equals(QueryCommand.RADIOLOGY.toString()))
            return radiologyHandler((JSONObject) json.get("doc"));
        else if (command.equals(QueryCommand.RESOURCE.toString()))
            return resourceHandler((JSONObject) json.get("doc"));
        else if (command.equals(QueryCommand.FILE.toString()))
            return fileHandler((JSONObject) json.get("doc"));
        else if (command.equals(QueryCommand.DISCONNECTION.toString()))
            return true;
        else
            return true;
    }

    public boolean authenticationHandler(String secretAuthenticate) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(TCPServer.class.getResource("/TestData/authentication_server.json").getPath()));
            System.out.println(TCPServer.class.getResource("/TestData/authentication_server.json").getPath());
            JSONObject authentication = (JSONObject) obj;
            String secret = (String) authentication.get("secret");
            if (secret.equals(secretAuthenticate)) {
                log.info("Authentication success");
                return false;
            } else {
                log.info("Authentication failed");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /** process user data, insert new user or update existed user */
    public boolean userHandler(JSONObject user) {
        Database db = new Database();
        String id = (String) user.get("Id");
        if (!isPatientExist(id)) {
            log.info("insert new patient");
            User patient = dataManager.processPatient(user);
            db.insertUser(patient);
        } else {
            log.info("update existed patient");
            User patient = dataManager.processPatient(user);
            db.updateUser(patient);
        }
        return false;
    }

    /** process appointment data, insert new appointment or update existed appointment */
    public boolean apptHandler(JSONObject appt) {
        Database db = new Database();
        String id = (String) appt.get("Id");
        if (!isApptExist(id)) {
            log.info("insert new appointment");
            Appointment apptointment = dataManager.processAppt(appt);
            db.insertAppointment(apptointment);
            try {
                PushNotification pn = new PushNotification();
                HashMap<String, String> pnResult = pn.sendNotification(apptointment);
                log.info("Push notification for new appointment");
                for (String token: pnResult.keySet()) {
                    log.info("FCMToken: " + token + " Push Notification Request:" + pnResult.get(token));
                }
            } catch (IOException e) {
                log.error("Push notification error");
            }
        } else {
            Appointment appointment = dataManager.processAppt(appt);
            Appointment apptintmentDB = db.getAppointment(id);
            // Justify whether the uploaded data equals to the ones in database
            if (!isApptEqual(appointment, apptintmentDB)) {
                //if not equal, update the database
                Database db1 = new Database();
                log.info("update exist appointment");
                db1.updateAppointment(appointment);
            }
        }
        return false;
    }

    public boolean doctorHandler(JSONObject dctor) {
        Database db = new Database();
        String id = (String) dctor.get("Id");
        if (!isDoctorExist(id)) {
            log.info("insert new doctor");
            Doctor doctor = dataManager.processDoctor(dctor);
            db.addDoctor(doctor);
        } else {
            log.info("update existed doctor");
            Doctor doctor = dataManager.processDoctor(dctor);
            db.updateDoctor(doctor);
        }
        return false;
    }

    public boolean hospitalHandler(JSONObject hspital) {
        Database db = new Database();
        String id = (String) hspital.get("Id");
        if (!isHospitalExist(id)) {
            log.info("insert new hospital");
            Hospital hospital = dataManager.processHospital(hspital);
            db.addHospital(hospital);
        } else {
            log.info("update existed hospital");
            Hospital hospital = dataManager.processHospital(hspital);
            db.updateHospital(hospital);
        }
        return false;
    }

    public boolean pathologyHandler(JSONObject pthology) {
        Database db = new Database();
        String id = (String) pthology.get("Id");
        if (!isPathologyExist(id)) {
            log.info("insert new pathology");
            Pathology pathology = dataManager.processPathology(pthology);
            db.addPathology(pathology);
        } else {
            log.info("update existed pathology");
            Pathology pathology = dataManager.processPathology(pthology);
            db.updatePathology(pathology);
        }
        return false;
    }

    public boolean radiologyHandler(JSONObject rdiology) {
        Database db = new Database();
        String id = (String) rdiology.get("Id");
        if (!isRadiologyExist(id)) {
            log.info("insert new radiology");
            Radiology radiology = dataManager.processRadiology(rdiology);
            db.addRadiology(radiology);
        } else {
            log.info("update existed radiology");
            Radiology radiology = dataManager.processRadiology(rdiology);
            db.updateRadiology(radiology);
        }
        return false;
    }

    public boolean resourceHandler(JSONObject rsource) throws IOException {

        if(rsource.containsKey("FileName")){			// if it contains FileName which means it is a pdf file
            System.out.println("File type: pdf");
            int bytesRead = 0;
            int current = 0;
            InputStream in = null;

            // file path configuration
            String resoucePath = TCPServer.class.getResource("/").getPath();
            String webappsDir = (new File(resoucePath,"../../")).getCanonicalPath();
            String fileName = (String)rsource.get("FileName");
            String userId = fileName.substring(fileName.lastIndexOf("-") + 1,
                    fileName.lastIndexOf(".")).trim();
            String eachFilePath = "/resource/" + userId + "/" + fileName;           // under the `resource` folder
            String filePath = webappsDir + eachFilePath;
            System.out.println(eachFilePath);
            System.out.println(filePath);

            try {
                in = connectionSocket.getInputStream();
                File newFile = new File(filePath);
                if (!newFile.exists()){
                    newFile.getParentFile().getParentFile().mkdir();
                    newFile.getParentFile().mkdir();
                    newFile.createNewFile();
                }
                DataInputStream clientData = new DataInputStream(in);
                OutputStream output = new FileOutputStream(filePath);

                long size = (long)rsource.get("FileSize");
                byte[] buffer = new byte[1024];

                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
                {
                    output.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                }
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String name = fileName.split("-")[1];

            // replace rsource to a new JSONObject
            Map<String, String> resourceMap = new HashMap<>();
            resourceMap.put("Uid", userId);
            resourceMap.put("Resource", eachFilePath);
            resourceMap.put("Name", name);

            rsource = new JSONObject(resourceMap);
            System.out.println(rsource.toJSONString());
        }

        Database db = new Database();
        String id = (String) rsource.get("Id");
        if (!isResourceExist(id)) {
            log.info("insert new resource");
            Resource resource = dataManager.processResource(rsource);
            db.insertResource(resource);
        } else {
            log.info("update existed resource");
            Resource resource = dataManager.processResource(rsource);
            db.updateResource(resource);
        }
        return false;
    }

    public boolean fileHandler(JSONObject file) throws IOException {
        int bytesRead = 0;
        int current = 0;
        InputStream in = null;

        String resoucePath = TCPServer.class.getResource("/").getPath();
        String webappsDir = (new File(resoucePath,"../../")).getCanonicalPath();
        String fileName = (String)file.get("FileName");
        String apptid = fileName.substring(fileName.lastIndexOf("-") + 1,
                fileName.lastIndexOf(".")).trim();
        String eachFilePath = "/result/" + apptid + "/" + fileName;
        String filePath = webappsDir + eachFilePath;
        System.out.println(eachFilePath);
        System.out.println(filePath);

        try {
            in = connectionSocket.getInputStream();
            File newFile = new File(filePath);
            if (!newFile.exists()){
                newFile.getParentFile().getParentFile().mkdir();
                newFile.getParentFile().mkdir();
                newFile.createNewFile();
            }
            DataInputStream clientData = new DataInputStream(in);
            OutputStream output = new FileOutputStream(filePath);

            long size = (long)file.get("FileSize");
            byte[] buffer = new byte[1024];


            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
            {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }
            output.flush();
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String id = apptid;
        com.medsec.entity.File appointmentFile = new com.medsec.entity.File().id(id)
                .apptid(apptid).title(fileName).link(eachFilePath);
        Database db = new Database();
        if (!isFileExist(id)){
            log.info("insert new file");
            db.insertFile(appointmentFile);
        } else {
            log.info("update existed file");
            db.updateFile(appointmentFile);
        }
        return false;
    }

    // Justify whether two Apponitments are identical
    public boolean isApptEqual(Appointment appt, Appointment apptDB){

        boolean flag = true;

        if (!appt.getId().equals(apptDB.getId())){
            flag = false;
        }
        
        if (!appt.getUid().equals(apptDB.getUid())){
            flag = false;
        }
        
        if (!appt.getDid().equals(apptDB.getDid())){
            flag = false;
        }
        
        if (!appt.getTitle().equals(apptDB.getTitle())){
            flag = false;
        }
        
        if (!appt.getDate_create().equals(apptDB.getDate_create())){
            flag = false;
        }
        
        if (!appt.getDate_change().equals(apptDB.getDate_change())){
            flag = false;
        }
        
        if (!appt.getDate().equals(apptDB.getDate())){
            flag = false;
        }
        
        if (!appt.getDuration().equals(apptDB.getDuration())){
            flag = false;
        }
        
        if (!checkEquals(appt.getDetail(), apptDB.getDetail())){
            flag = false;
        }
        
        if (!checkEquals(appt.getNote(), apptDB.getNote())){
            flag = false;
        }
        
        return flag;
    }

    public boolean checkEquals(String newRecord, String dbRecord){
        if (newRecord == null){
            newRecord = "";
        }
        if (dbRecord == null){
            dbRecord = "";
        }
        boolean results = newRecord.equals(dbRecord);
        return results;
    }

    /** check if the patient is already in the database */
    public boolean isPatientExist(String id){
        Database db = new Database();
        User patient = db.getUserById(id);
        System.out.println(patient != null);
        return patient != null;
    }

    /** check if the appointment is already in the database */
    public boolean isApptExist(String id){
        Database db = new Database();
        Appointment appt = db.getAppointment(id);
        return appt != null;
    }

    /** check if the doctor is already in the database */
    public boolean isDoctorExist(String id){
        Database db = new Database();
        Doctor dctor = db.selectOneDoctor(id);
        return dctor != null;
    }

    /** check if the hospital is already in the database */
    public boolean isHospitalExist(String id){
        Database db = new Database();
        Hospital hspital = db.selectOneHospital(id);
        return hspital != null;
    }

    /** check if the pathology is already in the database */
    public boolean isPathologyExist(String id){
        Database db = new Database();
        Pathology pthology = db.selectOnePathology(id);
        return pthology != null;
    }

    /** check if the radiology is already in the database */
    public boolean isRadiologyExist(String id){
        Database db = new Database();
        Radiology rology = db.selectOneRadiology(id);
        return rology != null;
    }

    /** check if the resource is already in the database */
    public boolean isResourceExist(String id){
        Database db = new Database();
        Resource rsource = db.getResource(id);
        return rsource != null;
    }

    /** check if the file is already in the database */
    public boolean isFileExist(String id){
        Database db = new Database();
        com.medsec.entity.File file = db.selectFileById(id);
        return file != null;
    }
}
