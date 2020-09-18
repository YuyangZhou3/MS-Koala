package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constant {
    public static String AUTO_PATH = "";
    public static long MONITOR_SECOND = 60;
    public static DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static boolean updateAppointment = true;
    public static boolean updateDoctor = true;
    public static boolean updateHospital = true;
    //public static boolean updateAppointment = false;
}
