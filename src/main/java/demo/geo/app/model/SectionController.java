package demo.geo.app.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import demo.geo.app.entities.Section;
import demo.geo.app.entities.GeologicalClass;

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
    public Section addSection(@Valid @RequestBody Section section) {
        return sectionService.addSection(section);
    }
    
    // Add new geoClass to existing Section (as POST-req to Geoclasses resource)
    @PostMapping("/{id}/geoclasses")
    public Section addGeoClass(@PathVariable long id, @Valid @RequestBody GeologicalClass geoClass) {
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
    }
    
    // Delete all records
    // initial version was ("/deleteAll")
    @DeleteMapping("")
    public void deleteAll() {
        sectionService.deleteAll();
    }
 
    // Returns a list of all Sections that have geologicalClasses with the specified code
    // using JPQL
    // TZ#2
    @GetMapping("/by-code")
    public List<Section> findByCode(@RequestParam(value = "code", required = true) String geoCode) {
        return sectionService.findSectionsByGeologicalCode(geoCode);
    }

}