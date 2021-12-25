package demo.geo.app.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;

import demo.geo.app.entities.Job;
import demo.geo.app.services.XlsService;
import demo.geo.app.enums.JStatus;
import demo.geo.app.enums.JType;

@RestController
@RequestMapping("/import")
public class ImportXlsController {

    private final XlsService xmlService;
        
    public ImportXlsController(XlsService xmlService) {
        this.xmlService = xmlService;
    }
    
    @PostMapping("")
    @ApiOperation("Returns Async Job and launches importing")
    public Job handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        Job job = xmlService.startJob(JType.IMPORT);
        xmlService.importXLS(job, file);
        return job;
    }
    
    @GetMapping("/{id}")
    @ApiOperation("Returns result of importing by Job ID")
    public JStatus getImportStatus(@PathVariable long id) {
        return xmlService.getJobStatus(JType.EXPORT, id);
    }
    
}
