package demo.geo.app.xls;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;

import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import demo.geo.app.entities.Section;
import demo.geo.app.exceptions.NotFoundException;
import demo.geo.app.model.SectionRepository;
import demo.geo.app.xls.enums.JStatus;
import demo.geo.app.xls.enums.JType;

@RestController
@RequestMapping("/export")
public class ExportXlsController {

    private final SectionRepository sectionRepository;

    private final XlsService xmlService;
    
    private final FileService fileService;
    
    @Autowired
    public ExportXlsController(SectionRepository sectionRepository, 
            XlsService xmlService, FileService fileService) {
        this.sectionRepository = sectionRepository;
        this.xmlService = xmlService;
        this.fileService = fileService;
    }
    
    @GetMapping("")
    @ApiOperation("Returns ID of the Async Job and launches exporting")
    public String exportSections() {
        Iterable<Section> sections = sectionRepository.findAll();
        if (sections.toString().equals("[]")) {
            throw new NotFoundException("! No any section found (DB is empty) !");
        }
        Job job = xmlService.startJob(JType.EXPORT);
        fileService.generateXLS(job);
        return "{ \"Job ID\" : " + job.getId().toString() + "}";
    }
    
    @GetMapping("/{id}")
    @ApiOperation("Returns result of parsed file by Job ID")
    public JStatus getExportStatus(@PathVariable long id) {
        return xmlService.getJobStatus(JType.IMPORT, id);
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
