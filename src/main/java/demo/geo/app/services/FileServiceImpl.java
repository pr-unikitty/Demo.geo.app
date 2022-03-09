package demo.geo.app.services;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import demo.geo.app.entities.Section;
import demo.geo.app.exceptions.NotFoundException;
import demo.geo.app.exceptions.UnprocessableException;
import demo.geo.app.dao.SectionRepository;
import demo.geo.app.dao.JobRepository;
import demo.geo.app.enums.JobStatus;
import demo.geo.app.enums.JobType;
import demo.geo.app.entities.Job;
import demo.geo.app.enums.JobFormat;

/**
 * Provides methods for {@link FileService} management
 */
@Service
public class FileServiceImpl implements FileService {

    private final SectionRepository sectionRepository;

    private final JobRepository jobRepository;

    private final XLSService xlsService;
    
    final static String TOM_CAT_DIR = System.getProperty("catalina.home");
    
    public FileServiceImpl (SectionRepository sectionRepository, JobRepository jobRepository,
            XLSService xlsService) {
        this.sectionRepository = sectionRepository;
        this.jobRepository = jobRepository;
        this.xlsService = xlsService;
    }
    
    /**
     * Starts new {@link Job} with status "IN_PROGRESS" 
     * (for TZ#3: /import OR /export)
     * 
     * @param jobType       import or export
     * @param jobFormat     file format
     * @return              started job
     */
    @Override
    public Job startJob(JobType jobType, JobFormat jobFormat) {
        Job newJob = new Job(jobType, JobStatus.IN_PROGRESS, jobFormat, LocalDateTime.now());
        if (newJob.getType() == JobType.EXPORT) {
            List<Section> sections = sectionRepository.findAll();
            if (sections.isEmpty()) {
                throw new NotFoundException("No any section found (DB is empty)");
            }
            xlsService.generateXLS(newJob);
        }
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
    @Override
    public JobStatus getJobStatus(JobType type, long jobId) {
        Job existingJob = jobRepository.getById(jobId);
        if (existingJob == null) {
            throw new NotFoundException("Job with this ID does not exist");
        }
        if (!existingJob.getType().equals(type)) {
            throw new UnprocessableException("Job type with this ID is not [" + type + "]");
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
    @Override
    public File exportXLS(Long id) throws MalformedURLException, FileNotFoundException {
        Job job = jobRepository.getById(id); 
        
        if (job.getStatus().equals(JobStatus.DONE) && job.getType().equals(JobType.EXPORT)) {
            String fileName = generateFileName(id, "xls");
            File file = new File(fileName);
            if (!file.exists()) {
                throw new NotFoundException("File " + fileName + " is not found");
            }
            return file;
        } else {
            throw new UnprocessableException("Job status is not [DONE] or job type is not [EXPORT]");
        }
    }

    /**
     * Loads user file and saves it to temp local dir of Tom Cat with name as JobID.xls
     * (for TZ#3: /import)
     * Then starts the parsing this file
     * !import add any records, even it's already exists in DB
     * 
     * @param job
     * @param file
     * @throws IOException
     */
    @Async
    @Override
    public void importXLS(Job job, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new UnprocessableException("File upload is failed: File is empty");
        }
        
        String fileName = generateFileName(job.getId(), "xls");
        File newFile = new File(fileName);
        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile))) {
            stream.write(file.getBytes());
            stream.flush();
        }
        
        xlsService.parseXLS(newFile, job);
    }

    @Override
    public void importCSV(Job job, File file) {

    }

    /**
     * Generates unique name of file using id of {@link Job} and current timestamp
     *
     * @param id    job id
     * @param ext   file extension
     * @return      full name of file like {dir_path}\\{id_timestamp.ext}
     */
    private String generateFileName(Long id, String ext) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-mm-dd_hh-mm-ss"));
        return TOM_CAT_DIR + File.separator + id + "_" + timestamp + "." + ext;
    }
}
