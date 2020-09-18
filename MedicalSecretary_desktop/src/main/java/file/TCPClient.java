package file;

import interfaces.UploadFile;
import javafx.concurrent.Task;
import app.MedicalSecretary;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;

public class TCPClient extends Task<Integer> {

    private static final String PATH = "auth";
    private static final String GENIE_DB_NAME = "TestData/user.json";
    private static final String CLIENT_KEY_STORE_PASSWORD = "client";
    private static final String CLIENT_TRUST_KEY_STORE_PASSWORD = "client";
    private static final String CLIENT_KEY_PATH = "/client_ks.jks";
    private static final String TRUST_SERVER_KEY_PATH = "/serverTrust_ks.jks";

    private UploadFile controller;
    private JSONWriter jsonWriter;
    private Socket clientSocket;

    public TCPClient( UploadFile controller) throws IOException {
        try{
            System.out.println("Server Connected");
            this.controller = controller;

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected Integer call() throws Exception {
        clientSocket = initSSLSocket();
        jsonWriter = JSONWriter.getInstance();
        jsonWriter.sendAuthentication(clientSocket);
        jsonWriter.sendUpdateData(clientSocket, controller.getFileList());
        if (clientSocket!=null)clientSocket.close();
        return 0;
    }

    @Override
    protected void succeeded() {
        controller.succeeded();
        controller.displayResultWindow("done", "Upload Success", "Upload Finished\n"+ jsonWriter.getResultStr());
        jsonWriter.setResultStr("");
    }
    @Override
    protected void cancelled() {
        controller.cancel();
        jsonWriter.setResultStr("");
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void failed() {
        controller.fail();
        String exception = "";
        for (int i = 0; i <getException().getStackTrace().length; i ++){
            StackTraceElement[] trace = getException().getStackTrace();
            for (StackTraceElement traceElement : trace)
                exception +="\tat " + traceElement +"\n";
        }
        controller.displayResultWindow("error", "upload fail", "Reason: " +exception);

        jsonWriter.setResultStr("");
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket initSSLSocket() {
        try{
            KeyStore ks = KeyStore.getInstance("JKS");
            //ks.load(new FileInputStream(PATH + CLIENT_KEY_PATH), CLIENT_KEY_STORE_PASSWORD.toCharArray());
            ks.load(MedicalSecretary.class.getClassLoader().getResourceAsStream(PATH + CLIENT_KEY_PATH), CLIENT_KEY_STORE_PASSWORD.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());

            KeyStore tks = KeyStore.getInstance("JKS");
            tks.load(MedicalSecretary.class.getClassLoader().getResourceAsStream(PATH + TRUST_SERVER_KEY_PATH), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(tks);

            SSLContext context = SSLContext.getInstance("SSL");

            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            Socket sslSocket = context.getSocketFactory().createSocket(MedicalSecretary.ip, MedicalSecretary.port);

            return sslSocket;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}