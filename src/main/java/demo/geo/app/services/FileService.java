package demo.geo.app.services;

import demo.geo.app.entities.Job;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.apache.poi.hssf.usermodel.*;

import java.util.*;
import java.io.IOException;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;

import demo.geo.app.entities.GeologicalClass;
import demo.geo.app.entities.Section;
import demo.geo.app.dao.SectionRepository;
import demo.geo.app.dao.JobRepository;
import demo.geo.app.xls.enums.JStatus;
import java.io.FileInputStream;

@Service
public class FileService {
    
    private final SectionRepository sectionRepository;

    private final JobRepository jobRepository;

    final static String TOM_CAT_DIR = System.getProperty("catalina.home");
    
    @Autowired
    public FileService (SectionRepository sectionRepository, JobRepository jobRepository) {
        this.sectionRepository = sectionRepository;
        this.jobRepository = jobRepository;
        System.out.println("*** "+ TOM_CAT_DIR +" ***");

    }
    
    /**
     * Launches exporting file (to local tmp dir of TomCat on the server)
     * (for TZ#3: /export)
     * @param job
     */
    @Async
    public void generateXLS(Job job) {
        try {
            job.setStatus(JStatus.IN_PROGRESS);
            Iterable<Section> sections = sectionRepository.findAll();

            HSSFWorkbook book = new HSSFWorkbook();
            HSSFSheet sheet = book.createSheet("Sections");
            HSSFRow headerRow = sheet.createRow(0);
            addCell(headerRow,"Section name");

            for (Section sec : sections) {
                // Section name
                HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
                addCell(row,sec.getName());
                
                // GeoClasses
                List<GeologicalClass> geoClasses = sec.getGeologicalClasses();
                for (GeologicalClass geoClass : geoClasses) {
                    if (row.getLastCellNum() == headerRow.getLastCellNum()) {
                        int i = Math.floorDiv(headerRow.getLastCellNum(), 2) + 1;
                        addCell(headerRow,String.format("Class %d name", i));
                        addCell(headerRow,String.format("Class %d code", i));
                    }
                    addCell(row,(geoClass.getName()));
                    addCell(row,(geoClass.getCode()));
                }
            }

            String exportFileName = TOM_CAT_DIR + File.separator + job.getId().toString() + ".xls";
            book.write(new FileOutputStream(exportFileName));
            job.setStatus(JStatus.DONE);
        } catch (IOException e) {
            job.setStatus(JStatus.ERROR);
        } finally {
            jobRepository.save(job);
        }
    }
    
    /**
     * Auxiliary method for adding cell to row in HSSF book
     * @param row current row
     * @param stringValue text to add in the cell
     */
    private void addCell(HSSFRow row, String stringValue) {
        HSSFCell newCell = row.createCell(Math.max(row.getLastCellNum(), 0));
        newCell.setCellValue(stringValue);
    }

    /**
     * Reads uploaded file and parses it as XLS book; 
     * Also sets up the job status 
     * (for TZ#3: /import)
     * 
     * @param file file to parse
     * @param job current job of importing
     */
    @Async
    public void parseXLS(File file, Job job) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            HSSFWorkbook xlsFile = new HSSFWorkbook(inputStream);
            inputStream.close();
            HSSFSheet sheet = xlsFile.getSheetAt(0); // header
            // Read body
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                // Read string
                HSSFRow currentRow = sheet.getRow(i);
                if (currentRow == null) {
                    continue;
                }
                String secName = currentRow.getCell(0).getStringCellValue();
                Section section = sectionRepository.save(new Section(secName));

                // Jumping for pairs {geoClass, geoCode}
                List<GeologicalClass> listOfGeoClasses = new ArrayList<>();
                for (int j = 1; j < currentRow.getLastCellNum(); j += 2) {
                    String geoClassName = readCell(currentRow, j);
                    String geoClassCode = readCell(currentRow, j+1);
                    if (geoClassName == null || geoClassCode == null) {
                        continue;
                    }
                    listOfGeoClasses.add(new GeologicalClass(section.getId(), geoClassName, geoClassCode));
                }
                section.addListOfGeoClasses(listOfGeoClasses);
                sectionRepository.save(section);
            }
            job.setStatus(JStatus.DONE);
            jobRepository.save(job);
        } catch (IOException e) {
            job.setStatus(JStatus.ERROR);
            jobRepository.save(job);
        }
    }
    
    /**
     * Auxiliary func for reading geoClassName or geoClassCode from HSSF cell
     * 
     * @param currentRow current row to read cell
     * @param cellNum number of cell in current row to read 
     * @return readed text value 
     */
    private String readCell (HSSFRow currentRow, int cellNum) {
        HSSFCell geoNameCell = currentRow.getCell(cellNum);
        if(geoNameCell == null) {
            return null;
        }
        return geoNameCell.getStringCellValue();
    }
    
}
