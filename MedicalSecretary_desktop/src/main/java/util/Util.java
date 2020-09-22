package util;
import java.io.*;

public class Util {

    public static void writeConfigFile(String name, String value) throws Exception {
        File iniFile = new File("config.ini");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(iniFile));
        String content = "";
        boolean flag = true;
        try {
            String strLine = "";
            boolean isExist = false;
            while ((strLine = bufferedReader.readLine()) != null) {
                String[] strArray = strLine.trim().split("=");
                if(strArray.length > 1){
                    if (strArray[0].trim().equalsIgnoreCase(name)){
                        content += name + " = " + value + "\n";
                        isExist =true;
                    }
                    else {
                        content += strLine+"\n";
                    }
                }
            }
            if (!isExist)content += name + " = " + value + "\n";
        } finally {
            bufferedReader.close();
        }

        PrintWriter pw = new PrintWriter(iniFile);
        try {
            pw.println(content);
        } finally {
            pw.close();
        }
    }

    public static void loadConfigFile() throws Exception{
        String strLine = "";
        File iniFile = new File("config.ini");
        if(!iniFile.exists()){
            PrintWriter pw = new PrintWriter(iniFile);
            try {
                pw.println("ServerIP = " + Constant.ip + "\nTCPPort = " + Constant.port);
            } finally {
                pw.close();
            }
        }else {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(iniFile));
            try {
                while ((strLine = bufferedReader.readLine()) != null) {
                    strLine = strLine.trim();
                    String[] strArray = strLine.split("=");
                    if (strArray.length > 1) {
                        if (strArray[0].trim().equalsIgnoreCase("uploadPath")) {
                            if (!strArray[1].isEmpty()) {
                                Constant.AUTO_PATH = strArray[1].trim();
                            }
                        } else if (strArray[0].trim().equalsIgnoreCase("ServerIP")) {
                            if (!strArray[1].isEmpty()) {
                                Constant.ip = strArray[1].trim();
                            }
                        } else if (strArray[0].trim().equalsIgnoreCase("TCPPort")) {
                            if (!strArray[1].isEmpty()) {
                                Constant.port = Integer.parseInt(strArray[1].trim());
                            }
                        }
                    }
                }
            } finally {
                bufferedReader.close();
            }
        }
    }
}
