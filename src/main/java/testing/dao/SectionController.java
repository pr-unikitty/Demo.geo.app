package testing.dao;

import testing.exceptions.OkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;

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
    @PostMapping("/add")
    public String add(@RequestParam(value = "section", required = true) String secName, 
            @RequestParam(value = "geoClassName", required = true) String geoName, 
            @RequestParam(value = "geoClassCode", required = true) String geoCode) {
       return sectionService.addSection(secName, geoName, geoCode);
    }
    
    // Find one section by ID and return info
    // initial version was ("/findById")
    // initial version was public String findById(@RequestParam(value = "id", required = true) Integer id) 
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
    
    // Delete one record by ID as GET request
    @DeleteMapping("/delete")
    public void delete(@RequestParam(value = "id", required = true) Integer id) {
        sectionService.delete(id);
       throw new OkException("Section deleted sucseccfully");
    }
    
    // Delete all records
    @DeleteMapping("/deleteAll")
    public void deleteAll() {
        sectionService.deleteAll();
       throw new OkException("All sections deleted sucseccfully");
    }

    // Add geoClass to Section
    @PutMapping("/addGeoclass")
    public void addGeoClass(@RequestParam(value = "section", required = true) String section,
            @RequestParam(value = "geoName", required = true) String geoName,
            @RequestParam(value = "geoCode", required = true) String geoCode ) {
        sectionService.addGeoclass(section, geoName, geoCode);
    }
 
    // Returns a list of all Sections that have geologicalClasses with the specified code
    // TZ#2
    @GetMapping("/sections/by-code")
    public String findByCode(@RequestParam(value = "code", required = true) String geoCode) 
            throws JsonProcessingException {
        return sectionService.findSectionsByGeoCode(geoCode);
    }
}