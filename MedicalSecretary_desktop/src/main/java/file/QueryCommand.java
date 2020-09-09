package file;
/**
 * Created by William Pan on 25/05/2018.
 * Modified by Robin Wang on 21/05/2020.
 */
public enum QueryCommand {
    AUTHENTICATION("Authentication",1),
    FILE("File",2),
    APPOINTMENT("Appointment",3),
    PATIENT("Patient",4),
    DOCTOR("Doctor",5),
    HOSPITAL("Hospital",6),
    PATHOLOGY("Pathology",7),
    RADIOLOGY("Radiology",8),
    RESOURCE("Resource",9),
    DISCONNECTION("Disconnection",10);


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