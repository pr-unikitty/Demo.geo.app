package demo.geo.app.services;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import demo.geo.app.entities.GeologicalClass;
import demo.geo.app.entities.Section;
import demo.geo.app.dao.SectionRepository;
import demo.geo.app.dao.JobRepository;
import demo.geo.app.enums.JobStatus;
import demo.geo.app.entities.Job;

/**
 * Provides methods for {@link XLSService} management
 */
@Service
public class XLSServiceImpl implements XLSService {
    
    private final SectionRepository sectionRepository;

    private final JobRepository jobRepository;
    
    private final SectionService sectionService;

    static final String TOM_CAT_DIR = System.getProperty("catalina.home");
    private static final Logger logger = LoggerFactory.getLogger(XLSService.class);
    
    public XLSServiceImpl(SectionRepository sectionRepository, JobRepository jobRepository,
                          SectionService sectionService) {
        this.sectionRepository = sectionRepository;
        this.jobRepository = jobRepository;
        this.sectionService = sectionService;
        logger.info("TomCat dir: "+ TOM_CAT_DIR);
    }
    
    /**
     * Launches exporting file (to local tmp dir of TomCat on the server)
     * (for TZ#3: /export)
     * @param job
     */
    @Async
    @Override
    public void generateXLS(Job job) {
        try {
            job.setStatus(JobStatus.IN_PROGRESS);
            Iterable<Section> sections = sectionRepository.findAll();

            HSSFWorkbook book = new HSSFWorkbook();
            HSSFSheet sheet = book.createSheet("Sections");
            HSSFRow headerRow = sheet.createRow(0);
            writeCell(headerRow,"Section name");

            for (Section sec : sections) {
                // Section name
                HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
                writeCell(row,sec.getName());
                
                // GeoClasses
                List<GeologicalClass> geoClasses = sec.getGeologicalClasses();
                for (GeologicalClass geoClass : geoClasses) {
                    if (row.getLastCellNum() == headerRow.getLastCellNum()) {
                        int i = Math.floorDiv(headerRow.getLastCellNum(), 2) + 1;
                        writeCell(headerRow,String.format("Class %d name", i));
                        writeCell(headerRow,String.format("Class %d code", i));
                    }
                    writeCell(row,(geoClass.getName()));
                    writeCell(row,(geoClass.getCode()));
                }
            }

            String exportFileName = TOM_CAT_DIR + File.separator + job.getId().toString() + ".xls";
            book.write(new FileOutputStream(exportFileName));
            job.setStatus(JobStatus.DONE);
        } catch (IOException e) {
            job.setStatus(JobStatus.ERROR);
        } finally {
            jobRepository.save(job);
        }
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
    @Override
    public void parseXLS(File file, Job job) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            HSSFWorkbook xlsFile = new HSSFWorkbook(inputStream);
            inputStream.close();
            HSSFSheet sheet = xlsFile.getSheetAt(0); // header

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow currentRow = sheet.getRow(i);
                if (currentRow == null) {
                    continue;
                }
                
                String secName = currentRow.getCell(0).getStringCellValue();
                if (sectionRepository.findByName(secName) != null) {
                    continue;
                }
                Section section = sectionRepository.save(new Section(secName));
                
                // Jumping for pairs {geoClass, geoCode}
                for (int j = 1; j < currentRow.getLastCellNum(); j += 2) {
                    String geoClassName = readCell(currentRow, j);
                    String geoClassCode = readCell(currentRow, j+1);
                    if (geoClassName == null || geoClassCode == null) {
                        continue;
                    }
                    sectionService.addGeoClassIfAbsent(section, new GeologicalClass(section.getId(), geoClassName, geoClassCode));
                }
                sectionRepository.save(section);
            }
            job.setStatus(JobStatus.DONE);
            jobRepository.save(job);
        } catch (IOException e) {
            job.setStatus(JobStatus.ERROR);
            jobRepository.save(job);
        }
    }
    
    /**
     * Auxiliary method for reading geoClassName or geoClassCode from HSSF cell
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
    
    /**
     * Auxiliary method for adding cell to row in HSSF book
     * @param row current row
     * @param stringValue text to add in the cell
     */
    private void writeCell(HSSFRow row, String stringValue) {
        HSSFCell newCell = row.createCell(Math.max(row.getLastCellNum(), 0));
        newCell.setCellValue(stringValue);
    }
}
