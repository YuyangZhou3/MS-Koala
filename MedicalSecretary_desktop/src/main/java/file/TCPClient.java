package file;

import interfaces.UploadFile;
import javafx.concurrent.Task;
import app.MedicalSecretary;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

public class TCPClient extends Task<Integer> {

    private static final Path PATH = Paths.get
            ("src/main/resources/auth").toAbsolutePath();
    private static final String GENIE_DB_NAME = "TestData/user.json";
    private static final String CLIENT_KEY_STORE_PASSWORD = "client";
    private static final String CLIENT_TRUST_KEY_STORE_PASSWORD = "client";
    private static final String CLIENT_KEY_PATH = "/client_ks.jks";
    private static final String TRUST_SERVER_KEY_PATH = "/serverTrust_ks.jks";

    //private Socket connectionSocket;
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
        jsonWriter.sendUpdateData(clientSocket, controller.getFileList());
        clientSocket.close();
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
        controller.displayResultWindow("error", "upload fail", "Reason: " +getException().fillInStackTrace().toString());
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
            ks.load(new FileInputStream(PATH + CLIENT_KEY_PATH), CLIENT_KEY_STORE_PASSWORD.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());

            KeyStore tks = KeyStore.getInstance("JKS");
            tks.load(new FileInputStream(PATH + TRUST_SERVER_KEY_PATH), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());
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