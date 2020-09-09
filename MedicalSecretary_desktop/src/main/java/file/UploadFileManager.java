package file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class UploadFileManager {
    private static String UPLOAD_PATH = null;
    private static File uploadedFile = null;

    protected UploadFileManager(String uploadPath){

        this.UPLOAD_PATH = uploadPath;
        this.uploadedFile = new File(this.UPLOAD_PATH);

    }

    public Element readHtmlFile() {

        Element htmlTable = null;

        try {
            Document doc = Jsoup.parse(uploadedFile, "UTF-8");

            htmlTable = doc.select("table").get(0);

        } catch (IOException e) {
            System.out.println("File not found!");
//            e.printStackTrace();
        }

        return htmlTable;
    }

    public HSSFSheet readExcelFile(){

        HSSFWorkbook workbook = null;
        HSSFSheet excelsheet = null;

        try {
            FileInputStream inputExcel = new FileInputStream(uploadedFile);
            workbook = new HSSFWorkbook(inputExcel);
            excelsheet = workbook.getSheetAt(0);

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return excelsheet;
    }

    public File readFile(){
        return uploadedFile;
    }
}
