package demo.geo.app.xls;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import demo.geo.app.entities.Section;
import demo.geo.app.exceptions.BadRequestException;
import demo.geo.app.exceptions.NotFoundException;
import demo.geo.app.exceptions.UnprocException;
import demo.geo.app.model.SectionRepository;

@RestController
public class XlsController {
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private XlsService xmlService;
    
    String tomCatDir = System.getProperty("catalina.home");
    
    // Returns ID of the Async Job and launches exporting
    @GetMapping("/export")
    public String exportSections() {
        Iterable<Section> sections = sectionRepository.findAll();
        if (sections.toString().equals("[]")) {
            throw new NotFoundException("! No any section found (DB is empty) !");
        }
        Job job = xmlService.startJob(JType.EXPORT);
        xmlService.generateXLS(job);
        return "{ \"Job ID\" : " + job.getId().toString() + "}";
    }
    
    // Returns result of parsed file by Job ID
    @GetMapping("/export/{id}")
    public String getExportStatus(@PathVariable Integer id) {
        Optional<Job> job = jobRepository.findById(id);
        if (job == null) {
            throw new NotFoundException("! Job with this ID is not found !");
        }
        if (id == null) {
            throw new BadRequestException("! Wrong ID !");
        }
        if (job.get().getType().equals(JType.IMPORT)) {
            throw new UnprocException("! Job type with this ID is not [export]!");
        }
        return xmlService.getJobStatus(id);
    }
    
    // Returns a file by Job ID
    @GetMapping(value = "/export/{id}/file")
    public Resource getXLSFileByJobId(@PathVariable Integer id) throws MalformedURLException {
        Optional<Job> job = jobRepository.findById(id);
        if (job == null) {
            throw new NotFoundException("! Job with this ID is not found !");
        }
        if (id == null) {
            throw new BadRequestException("! Wrong ID !");
        }
        if (job.get().getType().equals(JType.IMPORT)) {
            throw new UnprocException("! Job type with this ID is not [export]!");
        }
        Resource resource = xmlService.exportXLS(id);
        return resource;
    }
    
    
    // Returns ID of the Async Job and launches importing
    @PostMapping(value = "/import")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            Job job = xmlService.startJob(JType.IMPORT);
            File newFile = xmlService.importXLS(job, file);
            xmlService.parseXLS(new FileInputStream(newFile), job);
            return "{ \"Job ID\" : " + job.getId().toString() + "}";
        } else {
            throw new UnprocException ("! File upload is failed: File is empty !");
        }
    }
    
    // Returns result of importing by Job ID
    @GetMapping("/import/{id}")
    public String getImportStatus(@PathVariable Integer id) {
        Optional<Job> job = jobRepository.findById(id);
        if (job == null) {
            throw new NotFoundException("! Job with this ID is not found !");
        }
        if (id == null) {
            throw new BadRequestException("! Wrong ID !");
        }
        if (job.get().getType().equals(JType.EXPORT)) {
            throw new UnprocException("! Job type with this ID is not [import]!");
        }
        return xmlService.getJobStatus(id);
    }
}
