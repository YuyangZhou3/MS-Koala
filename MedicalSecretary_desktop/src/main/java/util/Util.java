package util;

import java.io.*;

public class Util {

    public static void writeConfigFile(){
        File iniFile = new File("config.ini");
        PrintWriter pw;
        try {
            pw = new PrintWriter(iniFile);
            pw.println("[uploadPath] = " + Constant.AUTO_PATH);
            pw.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void loadConfigFile() throws Exception{
        String strLine = "";
        if (!new File("config.ini").isFile()) return;
        BufferedReader bufferedReader = new BufferedReader(new FileReader("config.ini"));
        try {
            while ((strLine = bufferedReader.readLine()) != null) {
                strLine = strLine.trim();
                String[] strArray = strLine.split("=");
                if(strArray.length > 1){
                    if (strArray[0].trim().equalsIgnoreCase("[uploadPath]")){
                        if(!strArray[1].isEmpty()){
                            Constant.AUTO_PATH = strArray[1].trim();
                        }
                    }
                }
            }
        } finally {
            bufferedReader.close();
        }
    }
}
