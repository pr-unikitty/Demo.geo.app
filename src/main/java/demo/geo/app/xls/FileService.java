package demo.geo.app.xls;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.apache.poi.hssf.usermodel.*;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.List;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import demo.geo.app.entities.GeologicalClass;
import demo.geo.app.entities.Section;
import demo.geo.app.exceptions.NotFoundException;
import demo.geo.app.exceptions.UnprocException;
import demo.geo.app.model.SectionRepository;
import demo.geo.app.xls.enums.JStatus;
import demo.geo.app.xls.enums.JType;

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
    
    
}
