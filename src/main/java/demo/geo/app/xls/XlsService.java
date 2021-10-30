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

@Service
public class XlsService {

    private final SectionRepository sectionRepository;

    private final JobRepository jobRepository;

    final static String TOM_CAT_DIR = System.getProperty("catalina.home");
    
    @Autowired
    public XlsService (SectionRepository sectionRepository, JobRepository jobRepository) {
        this.sectionRepository = sectionRepository;
        this.jobRepository = jobRepository;
    }
    
    /**
     * Starts new {@link Job} with status "IN_PROGRESS" 
     * (for TZ#3: /import OR /export)
     * 
     * @param jobType type of Job - import or export
     * @return started job
     */
    public Job startJob(JType jobType) {
        Job newJob = new Job(jobType, JStatus.IN_PROGRESS, LocalDateTime.now());
        return jobRepository.save(newJob);
    }

    /**
     * Returns result of processing by JobID ("DONE", "IN PROGRESS", "ERROR") 
     * (for TZ#3: /export/id or /import/id)
     * 
     * @param type type of job must match with the one specified in the controller
     * it's needed to stop showing import-file status on request /export/id and vice versa 
     * @param jobId id of job to show status
     * @return status of job
     */
    public JStatus getJobStatus(JType type, long jobId) {
        Job existingJob = jobRepository.getById(jobId);
        if (existingJob == null) {
            throw new NotFoundException("! Job with this ID is not found !");
        }
        if (existingJob.getType().equals(type)) {
            throw new UnprocException("! Job type with this ID is not [" + type + "]!");
        }
        return existingJob.getStatus();
    }
    
    /**
     * Returns exporting file by ID of {@link Job}; name of file is obtained by: id.xls 
     * (for TZ#3: /export/id/file)
     * 
     * @param id job id of exporting file
     * @return file 'id.xls' of job
     * @throws MalformedURLException
     * @throws FileNotFoundException
     */
    public File exportXLS(Long id) throws MalformedURLException, FileNotFoundException {
        Job job = jobRepository.getById(id); 
        
        if (job.getStatus().equals(JStatus.DONE) && job.getType().equals(JType.EXPORT)) {
            String fileName = TOM_CAT_DIR + File.separator + id.toString() + ".xls";
            File file = new File(fileName);
            if(!file.exists()) {
                throw new NotFoundException("! File " + fileName + " is not found !");
            }
            return file;
        } else {
            throw new UnprocException("! Job status is not DONE or job type is not EXPORT !");
        }
    }
    
    // For TZ#3: /import
    // load user file and save it to temp.local dir with name as JobID.xls
    // !import add any records, even it's already exists in DB
    @Async
    public File importXLS(Job job, MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        String fileName = job.getId().toString() + ".xls";
        File newFile = new File(TOM_CAT_DIR + File.separator + fileName);
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile))) {
            stream.write(fileBytes);
        }
        //System.out.println("File is saved under: " + TOM_CAT_DIR + File.separator + file.getOriginalFilename());
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
    
    // Auxiliary func for reading geoClassName or geoClassCode from HSSF cell
    private String readCell (HSSFRow currentRow, int cellNum) {
        HSSFCell geoNameCell = currentRow.getCell(cellNum);
        if(geoNameCell == null) {
            return null;
        }
        return geoNameCell.getStringCellValue();
    }
}
