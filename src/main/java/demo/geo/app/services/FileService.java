package demo.geo.app.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import demo.geo.app.entities.Job;
import demo.geo.app.enums.*;

public interface FileService {
    
    Job startJob(JobType jobType, JobFormat jobFormat);
    JobStatus getJobStatus(JobType type, long jobId);
    File exportXLS(Long id) throws MalformedURLException, FileNotFoundException;
    void importXLS(Job job, MultipartFile file) throws IOException;
    void importCSV(Job job, File file);
    
}
