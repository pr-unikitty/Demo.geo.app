package demo.geo.app.xls;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.ApiOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import demo.geo.app.exceptions.UnprocException;
import demo.geo.app.xls.enums.JStatus;
import demo.geo.app.xls.enums.JType;

@RestController
@RequestMapping("/import")
public class ImportXlsController {

    private final JobRepository jobRepository;

    private final XlsService xmlService;
    
    @Autowired
    public ImportXlsController(JobRepository jobRepository, XlsService xmlService) {
        this.jobRepository = jobRepository;
        this.xmlService = xmlService;
    }
    
    @PostMapping("")
    @ApiOperation("Returns ID of the Async Job and launches importing")
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
    
    @GetMapping("/{id}")
    @ApiOperation("Returns result of importing by Job ID")
    public JStatus getImportStatus(@PathVariable long id) {
        return xmlService.getJobStatus(JType.EXPORT, id);
    }
    
}
