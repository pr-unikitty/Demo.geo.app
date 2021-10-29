package demo.geo.app.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import demo.geo.app.entities.GeologicalClass;

import javax.validation.Valid;

import demo.geo.app.entities.Section;
import demo.geo.app.exceptions.BadRequestException;
import demo.geo.app.exceptions.OkException;
import java.util.List;

@RestController
@RequestMapping("/sections")
public class SectionController {
    
    private final SectionService sectionService;
    
    @Autowired
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }
    
    // Add Section with one geoClass
    // initial version was ("/add")
    @PostMapping("")
    public Section add(@Valid @RequestBody Section section) {
        Section newSection = sectionService.addSection(section);
        //sectionService.addGeoclass(newSection.getId(), section.getGeologicalClasses().get(0));
        return newSection;
    }
    
    // Add new geoClass to existing Section (as POST-req to Geoclasses resource)
    @PostMapping("/{id}/geoclasses")
    public String addGeoClassPost(@PathVariable long id, @Valid @RequestBody GeologicalClass geoClass) {
        return sectionService.addGeoclass(id, geoClass);
    }
    
    // Find one section by ID and return info
    // initial version was ("/findById")
    @GetMapping("/{id}")
    public Section findById(@PathVariable long id) {
        return sectionService.findOne(id);
    }
    
    // Find all records in DB
    // initial version was ("showAll")
    @GetMapping("")
    public List<Section> findAll() {
       return sectionService.findAll();
    }
    
    // Delete one record by ID
    // initial version was ("/delete")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        sectionService.delete(id);
       throw new OkException("Section deleted sucseccfully");
    }
    
    // Delete all records
    // initial version was ("/deleteAll")
    @DeleteMapping("")
    public void deleteAll() {
        sectionService.deleteAll();
       throw new OkException("All sections deleted sucseccfully");
    }
 
    // Returns a list of all Sections that have geologicalClasses with the specified code
    // using JPQL
    // TZ#2
    @GetMapping("/by-code")
    public String findByCode(@RequestParam(value = "code", required = true) String geoCode) 
            throws JsonProcessingException {
        if (geoCode.equals("")) {
            throw new BadRequestException("! Argument must not be empty !");
        }
        return sectionService.findSectionsByGeologicalCode(geoCode);
    }

}