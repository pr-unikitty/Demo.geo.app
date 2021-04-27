package testing.xls;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;



@Service
public class XlsService {

    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private JobRepository jobRepository;

    String tomCatDir = System.getProperty("catalina.home");
    
    // For TZ#3: /import OR /export
    // Starting new job with status "IN_PROGRESS"
    public Job startJob(JType jobType) {
        Job newJob = new Job(jobType);
        newJob.setStatus(JStatus.IN_PROGRESS);
        newJob.setDateTime(LocalDateTime.now());
        jobRepository.save(newJob);
        return newJob;
    }

    // For TZ#3: /export/id
    // Returns result of processing by JobID ("DONE", "IN PROGRESS", "ERROR") 
    public String getJobStatus(Integer jobId) {
        Job job = jobRepository.findOne(jobId);
        if (job == null) {
            throw new NotFoundException("! Job with this ID is not found !");
        }
        return "{ \"Job status\" : \"" + job.getStatus().toString() + "\"}";
    }

    // For TZ#3: /export
    // Launches exporting file (to local tmp dir of TomCat on the server)
    // transmiting the file is carried out due to GET: /export/id/file
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
            // Finish writing a file
            String exportFileName = tomCatDir + File.separator + job.getId().toString() + ".xls";
            book.write(new FileOutputStream(exportFileName));
            job.setStatus(JStatus.DONE);
            jobRepository.save(job);

        } catch (Exception e) {
            job.setStatus(JStatus.ERROR);
            jobRepository.save(job);
        }
    }
    
    // For TZ#3: /export/id/file
    // !name of file is obtained by: id.xls 
    public Resource exportXLS(Integer id) throws MalformedURLException {
        JStatus status = jobRepository.findOne(id).getStatus();
        if (status.equals(JStatus.DONE)) {
            String fileName = tomCatDir + File.separator + id.toString() + ".xls";
            Resource resource = new UrlResource(Paths.get(fileName).resolve(fileName).normalize().toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new NotFoundException("File " + fileName + " is not found.");
            }
        }
        if (status.equals(JStatus.IN_PROGRESS)) {
            throw new ExportInProcessException("! Export is still in process !");
        }
        throw new ExportErrorException("! Export job ended with error !");
    }
    
    // For TZ#3: /import
    // load user file and save it to temp.local dir with name as JobID.xls
    // !import add any records, even it's already exists in DB
    @Async
    public File importXLS(Job job, MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        String fileName = job.getId().toString() + ".xls";
        File newFile = new File(tomCatDir + File.separator + fileName);
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile))) {
            stream.write(fileBytes);
        }
        //System.out.println("File is saved under: " + tomCatDir + File.separator + file.getOriginalFilename());
        return newFile;
    }
    
    // For TZ#3: /import
    // Read uploaded user file and parse it as XML book
    @Async
    public void parseXLS(InputStream inputStream, Job job) {
        try {
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
    
    // Auxiliary func for adding cell to row in HSSF book
    private void addCell(HSSFRow row, String value) {
        HSSFCell newCell = row.createCell(Math.max(row.getLastCellNum(), 0));
        newCell.setCellValue(value);
    }
    
    // Auxiliary func for reading geoClassName or geoClassCode from HSSF cell
    private String readCell (HSSFRow currentRow, int cellNum) {
        HSSFCell geoNameCell = currentRow.getCell(cellNum);
        if(geoNameCell == null) {
            return null;
        }
        return geoNameCell.getStringCellValue();
    }
}
