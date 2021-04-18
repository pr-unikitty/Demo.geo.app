package testing.xml;

import testing.exceptions.*;
import testing.dao.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.apache.poi.hssf.usermodel.*;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;



@Service
public class XmlService {

    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private XmlDataBase xmlDBService;

    
    // Starting new job
    public Job startJob(JType jobType) {
        Job newJob = new Job(jobType);
        newJob.setStatus(JStatus.IN_PROGRESS);
        newJob.setDateTime(LocalDateTime.now());
        jobRepository.save(newJob);
        return newJob;
    }

    // Fore TZ#3: /export/id
    // Returns result of processing by JobID ("DONE", "IN PROGRESS", "ERROR") 
    public JStatus getJobStatus(Integer jobId) {
        Job job = jobRepository.findOne(jobId);
        // Exeption
        if (job == null) 
            throw new NotFoundException("! Job with this ID is not found !");
        return job.getStatus();
    }
    
    // For TZ#3: /export/id/file
    // !name of file is obtained by: id.xls 
    public Resource downloadXLSFile(Integer id) {
        if (getJobStatus(id).equals(JStatus.DONE)) 
            return xmlDBService.loadResource(id.toString() + ".xls");
        if (getJobStatus(id).equals(JStatus.IN_PROGRESS)) 
            throw new ExportInProcessException("! Export is still in process !");
        throw new ExportErrorException("! Export job ended with error !");
    }

    // For TZ#3: /export
    @Async
    public void exportXLS(Job job) {
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
            // Finish writing a file
            String exportFileName = job.getId().toString() + ".xls";
            xmlDBService.storeExportFile(book, exportFileName);
            job.setStatus(JStatus.DONE);
            jobRepository.save(job);

        } catch (Exception e) {
            job.setStatus(JStatus.ERROR);
            jobRepository.save(job);
        }
    }
    
    // Fore TZ#3: /import
    // !import add any records, even it's already exists in DB
    @Async
    public void importXLS(InputStream inputStream, Job job, String importFileName) {
        try {
            HSSFWorkbook xlsFile = new HSSFWorkbook(inputStream);
            inputStream.close();
            // Read header
            HSSFSheet sheet = xlsFile.getSheetAt(0);
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
                for (int c = 1; c < currentRow.getLastCellNum(); c += 2) {
                    // Name
                    HSSFCell geoNameCell = currentRow.getCell(c);
                    if(geoNameCell == null)
                        continue;
                    String geoClassName = geoNameCell.getStringCellValue();
                    // Code
                    HSSFCell geoCodeCell = currentRow.getCell(c + 1);
                    if(geoCodeCell == null)
                        continue;
                    String geoClassCode = geoCodeCell.getStringCellValue();
                    
                    listOfGeoClasses.add(new GeologicalClass(section, geoClassName, geoClassCode));
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
    
    // Auxiliary func to add cell to row in HSSF book
    private void addCell(HSSFRow row, String value) {
        HSSFCell newCell = row.createCell(Math.max(row.getLastCellNum(), 0));
        newCell.setCellValue(value);
    }
}