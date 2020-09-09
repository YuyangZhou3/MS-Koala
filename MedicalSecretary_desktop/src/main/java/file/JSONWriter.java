package file;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class JSONWriter {
    private static JSONWriter instance = null;
    private final String AUTH_JSON =
            "src/main/resources/auth/TestData/authentication_client.json";
    private String resultStr = "";

    //private long lastUpdate = 0;

    protected JSONWriter() {
        // Exists only to defeat instantiation.
    }

    public static JSONWriter getInstance() throws IOException {
        if (instance == null) {
            instance = new JSONWriter();
        }
        return instance;
    }

    public String getResultStr(){
        return resultStr;
    }
    public void setResultStr(String resultStr){
        this.resultStr = resultStr;
    }

    /**
     * Adds Commands for data to update
     *
     * @param connectionSocket
     * @throws Exception
     */
    public void sendUpdateData(Socket connectionSocket, List<File> files) throws
            Exception {
        sendAuthentication(connectionSocket);
        for (File file : files) {
            String filename = file.getName();
            String ext = filename.substring(filename.lastIndexOf(".") + 1);
            boolean isSuccess = false;
            if (ext.equalsIgnoreCase("html")) {
                isSuccess = sendHtml(connectionSocket, QueryCommand.getCommandName(filename), file.getAbsolutePath());
            } else if (ext.equalsIgnoreCase("xls")) {
                isSuccess = sendHtml(connectionSocket,  QueryCommand.getCommandName(filename), file.getAbsolutePath());
            } else if (ext.equalsIgnoreCase("pdf")) {
                isSuccess = sendFile(connectionSocket, QueryCommand.getCommandName(filename), file.getAbsolutePath());
            }
            if (isSuccess){
                resultStr += "'"+filename + "'" + " has been uploaded.\n";
            }else{
                resultStr += "'"+filename + "'" + " failed.\n";
            }
        }
        sendDisconnect(connectionSocket);
        System.out.println("Upload Finished!");
    }

    /**
     * After established connection, authentication first
     */
    private void sendAuthentication(Socket connectionSocket) {
        try {
            OutputStream os = connectionSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(AUTH_JSON));
            JSONObject jsonObject = (JSONObject) obj;
            jsonObject.put("command", QueryCommand.AUTHENTICATION.toString());
//            String encryptedMsg = SymmetricEncrypt.getInstance().encrypt(jsonObject.toString());
            dos.writeUTF(jsonObject + "\n");
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send disconnection after completing all DB queries
     */
    private void sendDisconnect(Socket connectionSocket) {
        try {
            OutputStream os = connectionSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("command", QueryCommand.DISCONNECTION.toString());
//            String encryptedMsg = SymmetricEncrypt.getInstance().encrypt(jsonObject.toString());
            dos.writeUTF(jsonObject + "\n");
            dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Extract html file data and send from client to the server
     */
    private boolean sendHtml(Socket connectionSocket, QueryCommand command, String uploadPath) {
        boolean isSuccess = false;
        try {
            OutputStream os = connectionSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            UploadFileManager uploadHtml = new UploadFileManager(uploadPath);
            System.out.println("Uploaded Success");
            Element htmlTable = uploadHtml.readHtmlFile();
            Elements htmlTrs = htmlTable.select("tr");

            Elements tdHeads = htmlTrs.get(0).select("td");

//            JSONArray jsonObjectDoc = new JSONArray();
            for (int i = 1; i < htmlTrs.size(); i++) {
                JSONObject msg = new JSONObject();
                msg.put("command", command.toString());

                JSONObject jsonObject = new JSONObject();
                Elements tds = htmlTrs.get(i).select("td");
                for (int j = 0; j < tds.size(); j++) {

                    Element pHead = tdHeads.get(j).select("p").get(0);
                    Element pContent = tds.get(j).select("font").get(0);

                    if (pContent.hasClass("p")) {
                        pContent = pContent.select("p").get(0);
                    }

                    if (pContent != null) {
                        if (!pContent.text().equals("")) {
                            jsonObject.put(pHead.text(), pContent.text());
                        } else {
                            jsonObject.put(pHead.text(), null);
                        }
                    } else {
                        jsonObject.put(pHead.text(), pContent);
                    }

                }
//                jsonObjectDoc.add(jsonObject);
                msg.put("doc", jsonObject);
                System.out.println(msg);
                dos.writeUTF(msg + "\n");
                dos.flush();

            }
            isSuccess = true;
        } catch (NullPointerException e) {
            System.out.println("File content not found!");
//                e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * Extract excel file data and send from client to the server
     */
    private boolean sendExcel(Socket connectionSocket, QueryCommand command, String uploadPath) {
        boolean isSuccess = false;
        try {
            OutputStream os = connectionSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            UploadFileManager uploadExcel = new UploadFileManager(uploadPath);
            System.out.println("Uploaded Success");
            HSSFSheet excelSheet = uploadExcel.readExcelFile();
            HSSFRow excelHeads = excelSheet.getRow(0);

            int rowNumber = excelSheet.getLastRowNum();
            System.out.println(rowNumber);
            for (int i = 1; i < rowNumber + 1; i++) {
                JSONObject msg = new JSONObject();
                msg.put("command", command.toString());

                JSONObject jsonObject = new JSONObject();
                HSSFRow excelRow = excelSheet.getRow(i);
                int cellNumber = excelRow.getLastCellNum();
                for (int j = 0; j < cellNumber; j++) {
                    HSSFCell excelHead = excelHeads.getCell(j);
                    HSSFCell excelCellContent = excelRow.getCell(j);

                    excelHead.setCellType(CellType.STRING);

                    if (excelCellContent != null) {
                        excelCellContent.setCellType(CellType.STRING);
                        jsonObject.put(excelHead.getStringCellValue(), excelCellContent.getStringCellValue());
                    } else {
                        jsonObject.put(excelHead.getStringCellValue(), excelCellContent);
                    }
                }

                msg.put("doc", jsonObject);
                System.out.println(msg);
                dos.writeUTF(msg + "\n");
                dos.flush();
            }
            isSuccess = true;
        } catch (NullPointerException e) {
            System.out.println("File content not found!");
//                e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * Send file data from client to the server
     */
    private boolean sendFile(Socket connectionSocket, QueryCommand command, String uploadPath) {
        boolean isSuccess = false;
        try {
            OutputStream os = connectionSocket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            UploadFileManager uploadedFile = new UploadFileManager(uploadPath);
            System.out.println("Uploaded Success");
            File myFile = uploadedFile.readFile();

            JSONObject msg = new JSONObject();
            JSONObject jsonObject = new JSONObject();

            msg.put("command", command.toString());
            jsonObject.put("FileName", myFile.getName());
            jsonObject.put("FileSize", myFile.length());
            msg.put("doc", jsonObject);
            System.out.println(msg);
            dos.writeUTF(msg + "\n");
            dos.flush();

            sendData(connectionSocket, myFile);
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * Send file as byte stream through socket
     */
    private void sendData(Socket connectionSocket, File myFile) throws IOException {
        //Send file
        byte[] mybytearray = new byte[(int) myFile.length()];

        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);

        DataInputStream dis = new DataInputStream(bis);
        dis.readFully(mybytearray, 0, mybytearray.length);

        OutputStream os = connectionSocket.getOutputStream();

        //Sending file name and file size to the server
        DataOutputStream dos = new DataOutputStream(os);
//          dos.writeUTF(myFile.getName());
//          dos.writeLong(mybytearray.length);
        dos.write(mybytearray, 0, mybytearray.length);
        dos.flush();

        //dos.close();
    }
}
