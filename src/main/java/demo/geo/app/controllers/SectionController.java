package demo.geo.app.controllers;

import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;

import javax.validation.Valid;
import java.util.List;

import demo.geo.app.entities.Section;
import demo.geo.app.entities.GeologicalClass;
import demo.geo.app.services.SectionService;

@RestController
@RequestMapping("sections")
public class SectionController {
    
    private final SectionService sectionService;
    
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }
    
    @PostMapping
    @ApiOperation("Adds Section with list of GeologicalClasses. "
            + "List of geoClasses can be null; geoClasses must have unique names and codes")
    public Section createSection(@Valid @RequestBody Section section) {
        return sectionService.createSection(section);
    }
    
    @PostMapping("{id}/geoclasses")
    @ApiOperation("Adds new geologicalClass to existing Section")
    public Section addGeoClass(@PathVariable long id, @Valid @RequestBody GeologicalClass geoClass) {
        return sectionService.addGeoclassOrThrow(id, geoClass);
    }
    
    @GetMapping("{id}")
    @ApiOperation("Finds one Section by ID")
    public Section findById(@PathVariable long id) {
        return sectionService.findOne(id);
    }
    
    @GetMapping
    @ApiOperation("Finds all Section by ID")
    public List<Section> findAll() {
       return sectionService.findAll();
    }
    
    @DeleteMapping("{id}")
    @ApiOperation("Deletes one Section by ID")
    public void deleteById(@PathVariable long id) {
        sectionService.deleteById(id);
    }
    
    @DeleteMapping
    @ApiOperation("Deletes all Section by ID")
    public void deleteAll() {
        sectionService.deleteAll();
    }
 
    @GetMapping("by-code")
    @ApiOperation("Returns a list of all Sections that have geologicalClasses with the specified code")
    public List<Section> findByGeologicalCode(@RequestParam(value = "code", required = true) String geoCode) {
        return sectionService.findSectionsByGeologicalCode(geoCode);
    }

}