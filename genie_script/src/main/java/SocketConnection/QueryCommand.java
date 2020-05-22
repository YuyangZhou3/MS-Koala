package SocketConnection;

/**
 * Created by William Pan on 25/05/2018.
 * Modified by Robin Wang on 21/05/2020.
 */
public enum QueryCommand {
    AUTHENTICATION("Authentication",1),
    APPOINTMENT("Appointment",2),
    PATIENT("Patient",3),
    DOCTOR("Doctor",4),
    HOSPITAL("Hospital",5),
    PATHOLOGY("Pathology",6),
    RADIOLOGY("Radiology",7),
    FILE("File",8),
    DISCONNECTION("Disconnection",9);

    private String name;
    private int index;

    QueryCommand(String name,int index){
        this.name = name;
        this.index = index;
    }

    /**
     * @return Returns the index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the Querycommand of which the name is contained within the input string.
     */
    public static QueryCommand getCommandName(String command) {
        for (QueryCommand qc : QueryCommand.values()) {
            if (command.contains(qc.getName())) {
                return qc;
            }
        }
        return null;
    };
}