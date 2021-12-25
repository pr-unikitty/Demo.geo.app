package demo.geo.app.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import demo.geo.app.entities.Job;
import demo.geo.app.enums.JStatus;
import demo.geo.app.enums.JType;

public interface XlsService {
    Job startJob(JType jobType);
    JStatus getJobStatus(JType type, long jobId);
    File exportXLS(Long id) throws MalformedURLException, FileNotFoundException;
    File importXLS(Job job, MultipartFile file) throws IOException;
}
