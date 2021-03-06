package demo.geo.app.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import io.swagger.annotations.ApiOperation;

import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import demo.geo.app.enums.JStatus;
import demo.geo.app.enums.JType;
import demo.geo.app.entities.Job;
import demo.geo.app.services.XlsService;

@RestController
@RequestMapping("/export")
public class ExportXlsController {

    private final XlsService xmlService;
        
    public ExportXlsController(XlsService xmlService) {
        this.xmlService = xmlService;
    }
    
    @GetMapping("")
    @ApiOperation("Returns Async Job and launches exporting DB to the xls file")
    public Job exportSections() {
        return xmlService.startJob(JType.EXPORT);
    }
    
    @GetMapping("/{id}")
    @ApiOperation("Returns result of parsed file by Job ID")
    public JStatus getExportStatus(@PathVariable long id) {
        return xmlService.getJobStatus(JType.EXPORT, id);
    }

    @GetMapping(value = "/{id}/file")
    @ApiOperation("Returns a file by Job ID")
    public @ResponseBody Resource getXLSFileByJobId(@PathVariable long id, HttpServletResponse response) 
            throws MalformedURLException, IOException {
        File file = xmlService.exportXLS(id);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        return new FileSystemResource(file);
    }
    
}
