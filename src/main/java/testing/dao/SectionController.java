package testing.dao;

import testing.exceptions.OkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import testing.exceptions.*;

@RestController
public class SectionController {
    
    @Autowired
    private SectionService sectionService;
    
    // Index page
    @RequestMapping("/")
    public String index() {
        return "Greetings from my testing Spring!";
    }
    
    // Add Section with one geoClass
    // initial version was ("/add")
    @PostMapping("/sections")
    public String add(@RequestParam(value = "section", required = true) String secName, 
            @RequestParam(value = "geoClassName", required = true) String geoName, 
            @RequestParam(value = "geoClassCode", required = true) String geoCode) {
        if (secName.equals("") || geoName.equals("") || geoCode.equals("")) {
            throw new BadRequestException("! No one argument can be empty !");
        }
        return sectionService.addSection(secName, geoName, geoCode);
    }
    
    // Add new geoClass to existing Section (as POST-req to Geoclasses resource)
    @PostMapping("/sections/{id}/geoclasses")
    public String addGeoClassPost(@PathVariable Integer id, 
            @RequestParam(value = "geoClassName", required = true) String geoName, 
            @RequestParam(value = "geoClassCode", required = true) String geoCode) {
        if (geoName.equals("") || geoCode.equals("")) {
            throw new BadRequestException("! No one argument can be empty !");
        }
        return sectionService.addGeoclass(id, geoName, geoCode);
    }
    
    // Find one section by ID and return info
    // initial version was ("/findById")
    @GetMapping("/sections/{id}")
    public String findById(@PathVariable Integer id) 
            throws JsonProcessingException {
        return sectionService.findOneToJSON(id);
    }
    
    // Find all records in DB
    // initial version was ("showAll")
    @GetMapping("/sections")
    public String findAll() {
       return sectionService.findAllToJSON();
    }
    
    // Delete one record by ID
    // initial version was ("/delete")
    @DeleteMapping("/sections/{id}")
    public void delete(@PathVariable Integer id) {
        sectionService.delete(id);
       throw new OkException("Section deleted sucseccfully");
    }
    
    // Delete all records
    // initial version was ("/deleteAll")
    @DeleteMapping("/sections")
    public void deleteAll() {
        sectionService.deleteAll();
       throw new OkException("All sections deleted sucseccfully");
    }

    // Add geoClass to Section (as PUT-req to Section resource)
    // initial version was ("/addGeoclass")
    @PutMapping("/sections/{id}")
    public void addGeoClass(@PathVariable Integer id,
            @RequestParam(value = "geoName", required = true) String geoName,
            @RequestParam(value = "geoCode", required = true) String geoCode ) {
        sectionService.addGeoclass(id, geoName, geoCode);
    }
 
    // Returns a list of all Sections that have geologicalClasses with the specified code
    // using JPQL
    // TZ#2
    @GetMapping("/sections/by-code")
    public String findByCode(@RequestParam(value = "code", required = true) String geoCode) 
            throws JsonProcessingException {
        if (geoCode.equals("")) {
            throw new BadRequestException("! Argument must not be empty !");
        }
        return sectionService.findSectionsByGeologicalCode(geoCode);
    }

}