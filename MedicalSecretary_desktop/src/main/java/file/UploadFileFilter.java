package file;

import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;

public class UploadFileFilter implements IOFileFilter {
    @Override
    public boolean accept(File file) {
        String filename = file.getName();
        try {
            QueryCommand type = QueryCommand.getCommandName(file.getName());
            if (type == null) throw new Exception();
            String ext = filename.substring(filename.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("html") ){
                return true;
            }else if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx")){
                return true;
            }else if(ext.equalsIgnoreCase("pdf")){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    @Override
    public boolean accept(File file, String s) {
        return false;
    }
}
