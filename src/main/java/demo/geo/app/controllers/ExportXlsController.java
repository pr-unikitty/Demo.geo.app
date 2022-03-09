package demo.geo.app.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import io.swagger.annotations.ApiOperation;

import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import demo.geo.app.enums.*;
import demo.geo.app.entities.Job;
import demo.geo.app.services.FileService;

@RestController
@RequestMapping("export/xls")
public class ExportXlsController {

    private final FileService xmlService;
        
    public ExportXlsController(FileService xmlService) {
        this.xmlService = xmlService;
    }
    
    @GetMapping
    @ApiOperation("Returns Async Job and launches exporting DB to the XLS-file")
    public Job exportSections() {
        return xmlService.startJob(JobType.EXPORT, JobFormat.XLS);
    }
    
    @GetMapping("{id}")
    @ApiOperation("Returns the Job as result of parsed file by ID")
    public JobStatus getExportStatus(@PathVariable long id) {
        return xmlService.getJobStatus(JobType.EXPORT, id);
    }

    @GetMapping("{id}/file")
    @ApiOperation("Returns the file by Job ID")
    public @ResponseBody Resource getXLSFileByJobId(@PathVariable long id, HttpServletResponse response) 
            throws MalformedURLException, IOException {
        File file = xmlService.exportXLS(id);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        return new FileSystemResource(file);
    }
    
}
