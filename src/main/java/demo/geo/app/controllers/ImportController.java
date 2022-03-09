package demo.geo.app.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.ApiOperation;

import java.io.File;
import java.io.IOException;

import demo.geo.app.entities.Job;
import demo.geo.app.services.FileService;
import demo.geo.app.enums.*;

@RestController
@RequestMapping("import")
public class ImportController {

    private final FileService fileService;
        
    public ImportController(FileService xmlService) {
        this.fileService = xmlService;
    }
    
    @PostMapping("xls")
    @ApiOperation("Returns Async Job and launches importing from XLS file")
    public Job handleFileUploadFromXLS(@RequestParam("file") MultipartFile file) throws IOException {
        Job job = fileService.startJob(JobType.IMPORT, JobFormat.XLS);
        fileService.importXLS(job, file);
        return job;
    }
    
    @GetMapping("xls/{id}")
    @ApiOperation("Returns result of importing by Job ID")
    public JobStatus getImportStatus(@PathVariable long id) {
        return fileService.getJobStatus(JobType.IMPORT, id);
    }

    @PostMapping("csv")
    @ApiOperation("Returns Async Job and launches importing from CSV file")
    public Job handleFileUploadFromCSV(@RequestParam("file") File file) throws IOException {
        Job job = fileService.startJob(JobType.IMPORT, JobFormat.CSV);
        fileService.importCSV(job, file);
        return job;
    }
}
