package demo.geo.app.model;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

import demo.geo.app.entities.GeologicalClass;
import demo.geo.app.entities.Section;
import demo.geo.app.exceptions.NotFoundException;
import demo.geo.app.exceptions.OkException;
import demo.geo.app.exceptions.UnprocException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {
    
    private final SectionRepository sectionRepository;
    
    private final GeologicalClassRepository geologicalClassRepository;
    
    @Autowired
    public SectionService(SectionRepository sectionRepository, 
            GeologicalClassRepository geologicalClassRepository) {
        this.sectionRepository = sectionRepository;
        this.geologicalClassRepository = geologicalClassRepository;
    }
    
    // Find one section by id and return info in correct form without id;
    // simple method shows ArrayList as mem's address
    public String findOneToJSON(long id) {
        Section section = sectionRepository.getById(id);
        if (section == null) 
            throw new NotFoundException("! Section with this ID is not found !");
        // With only fields with @Expose
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create(); 
        return gson.toJson(section);
    }
    
    public Section findOne(long id) {
        return sectionRepository.findById(id).orElse(null);
    }
    
    // Find each section and show it with general {}
    // (uses previous function findOneToJSON)
    public String findAllToJSON() {
        Iterable<Section> sections = sectionRepository.findAll();
        if (sections.toString().equals("[]")) {
            throw new NotFoundException("! No any section found (DB is empty) !");
        }
        String output = "";
        for (Section sec : sections) {
            System.out.println("*** "+ sec +" ***");
            output += findOneToJSON(sec.getId());
        }
        return output;
    }
    
    public List<Section> findAll() {
        return sectionRepository.findAll();
    }
    
    // Add new Section with one couple {geoClassName, geoClassCode}
    public Section addSection(Section section) {
        Section existingSection = sectionRepository.findByName(section.getName());
        if (existingSection != null) {
            throw new UnprocException("! Section with this name is already exist !");
        }
        Section newSection = new Section();
        newSection.setName(section.getName());
        //newSection.setGeologicalClasses(section.getGeologicalClasses());
        //newSection.addGeoClass(new GeologicalClass(newSection.getId(), 
        //        section.getGeologicalClasses().get(0).getName(), 
        //        section.getGeologicalClasses().get(0).getCode()));
         
        return sectionRepository.save(newSection);
    }
        
    // Delete one record by ID    
    public void delete(long id) {
        if (sectionRepository.getById(id) == null) {
            throw new NotFoundException("! Section with this ID can not be deleted because is not found !");
        }
        sectionRepository.deleteById(id);
    }

    // Delete all sections with geoClasses  
    public void deleteAll() {
        Iterable<Section> sections = sectionRepository.findAll();
        if (sections.toString().equals("[]")) {
            throw new NotFoundException("! No any section found (DB is empty) !");
        }
        // Concatenation with general "{" and "}" to list of each record
        for (Section sec : sections) {
            sectionRepository.deleteById(sec.getId());
        }
    }
    
    // Add geoClass to Section
    public String addGeoclass(long id, GeologicalClass geoClass) {
        Section existingSection = sectionRepository.getById(id);
        if (existingSection == null) {
            throw new NotFoundException("! Section with this ID does not exists !");
        }
        // Check existing class with this name in existing section
        List<GeologicalClass> geoClasses = existingSection.getGeologicalClasses();
        if (geoClasses != null) {
            for (GeologicalClass existingGeoClass : geoClasses) {
                // Exceptions
                if (existingGeoClass.getName().equals(geoClass.getName())) {
                    throw new UnprocException("! GeologicalClass witn this Name already exists in this Section!");
                }
                if (existingGeoClass.getCode().equals(geoClass.getCode())) {
                    throw new UnprocException("! GeologicalClass witn this Code already exists in this Section!");
                }
            } 
        }
        // If geo is not exists, add to existing sec
        existingSection.addGeoClass(new GeologicalClass(existingSection.getId(), geoClass.getName(), geoClass.getCode()));
        sectionRepository.save(existingSection);
        throw new OkException("Section updated sucseccfully (GeologicalClass added in existing Section)");
    }
    
    // Returns a list of all Sections that have geologicalClasses with the specified code
    // TZ#2
    public String findSectionsByGeologicalCode (String geoCode) {
        List<Section> neededSections = sectionRepository.findSectionsByGeoCode(geoCode);
        // Exeption
        if (neededSections.toString().equals("[]")) {
            throw new NotFoundException("! No any section found !");
        }
        // List to JSON format
        String output = "";
        for (Section sec : neededSections) {
            output += findOneToJSON(sec.getId());
        }
        return output;
    }

}
