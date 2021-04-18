package testing.xml;

import testing.dao.*;
import testing.exceptions.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;



@RestController
public class XmlController {
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private XmlDataBase xmlDataService;
    @Autowired
    private XmlService xmlService;
    
    // Returns ID of the Async Job and launches exporting
    @GetMapping("/export")
    public String exportSections() {
        // Exeption
        Iterable<Section> sections = sectionRepository.findAll();
        if (sections.toString().equals("[]")) 
            throw new NotFoundException("! No any section found (DB is empty) !");
        Job job = xmlService.startJob(JType.EXPORT);
        xmlService.exportXLS(job);
        throw new OkException("Job with ID=[" + job.getId().toString() + "] started sucsessfully");
    }
    
    // Returns result of parsed file by Job ID
    @GetMapping("/export/{id}")
    public String getExportStatus(@PathVariable Integer id) {
        // Exeption
        Job job = jobRepository.findOne(id);
        if (job == null) 
            throw new NotFoundException("! Job with this ID is not found !");
        if (id == null) {
            throw new BadRequestException("! Wrong ID !");
        }
        if (job.getType().equals(JType.IMPORT))
            throw new UnprocException("! Job type with this ID is not [export]!");
        throw new OkException("Status of Job with ID=[" + job.getId().toString() + 
                "] is [" + xmlService.getJobStatus(id).toString() + "]");
    }
    
    // Returns a file by Job ID
    @GetMapping(value = "/export/{id}/file")
    public Resource getXLSFileByJobId(@PathVariable Integer id) {
        // Exeption
        Job job = jobRepository.findOne(id);
        if (job == null) 
            throw new NotFoundException("! Job with this ID is not found !");
        if (id == null) {
            throw new BadRequestException("! Wrong ID !");
        }
        if (job.getType().equals(JType.IMPORT))
            throw new UnprocException("! Job type with this ID is not [export]!");
        Resource resource = xmlService.downloadXLSFile(id);
        return resource;
    }
    
    // Returns ID of the Async Job and launches importing
    @PostMapping(value = "/import")
    public String importXLS(@RequestParam("file") File file) throws IOException {
        Job job = xmlService.startJob(JType.IMPORT);
        String fileName = job.getId().toString() + ".xls";
        xmlDataService.storeImportFile(new FileInputStream(file), fileName);
        xmlService.importXLS(new FileInputStream(file), job, fileName);
        throw new OkException("Job with ID=[" + job.getId().toString() + "] started sucsessfully");

    }
    
    // Returns result of importing by Job ID
    @GetMapping("/import/{id}")
    public String getImportStatus(@PathVariable Integer id) {
        // Exeption
        Job job = jobRepository.findOne(id);
        if (job == null) 
            throw new NotFoundException("! Job with this ID is not found !");
        if (id == null) {
            throw new BadRequestException("! Wrong ID !");
        }
        if (job.getType().equals(JType.EXPORT))
            throw new UnprocException("! Job type with this ID is not [import]!");
        throw new OkException("Status of Job with ID=[" + job.getId().toString() + 
                "] is [" + xmlService.getJobStatus(id).toString() + "]");
    }
}
