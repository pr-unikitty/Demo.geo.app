package testing.dao;

import testing.exceptions.NotFoundException;
import testing.exceptions.OkException;
import testing.exceptions.UnprocException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.*;

@Service
public class SectionService {
    
    @Autowired
    private SectionRepository sectionRepository;
    
    // Find one section by id and return info in correct form without id;
    // simple method shows ArrayList as mem's address
    public String findOneToJSON(Integer id) {
        Section section = sectionRepository.findOne(id);
        if (section == null) 
            throw new NotFoundException("! Section with this ID is not found !");
        // With only fields with @Expose
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create(); 
        return gson.toJson(section);
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
            output += findOneToJSON(sec.getId());
        }
        return output;
    }
    
    // Add new Section with one couple {geoClassName, geoClassCode}
    public String addSection(String secName, String geoName, String geoCode) {
        if (sectionRepository.findByName(secName).size() != 0) {
            throw new UnprocException("! Section with this name is already exist !");
        }
        Section section = new Section(secName);
        GeologicalClass geoClass = new GeologicalClass(section, geoName, geoCode);
        section.addGeoClass(geoClass);
        sectionRepository.save(section);
        return findOneToJSON(section.getId());
    }
        
    // Delete one record by ID    
    public void delete(Integer id) {
        if (sectionRepository.findOne(id) == null) {
            throw new NotFoundException("! Section with this ID can not be deleted because is not found !");
        }
        sectionRepository.delete(id);
    }

    // Delete all sections with geoClasses  
    public void deleteAll() {
        Iterable<Section> sections = sectionRepository.findAll();
        if (sections.toString().equals("[]")) {
            throw new NotFoundException("! No any section found (DB is empty) !");
        }
        // Concatenation with general "{" and "}" to list of each record
        for (Section sec : sections) {
            sectionRepository.delete(sec.getId());
        }
    }
    
    // Add geoClass to Section
    public String addGeoclass(Integer id, String geoName, String geoCode) {
        Section sections = sectionRepository.findOne(id);
        if (sections == null) {
            throw new NotFoundException("! Section with this ID do not exists !");
        }
        // Check existing class with this name in existing section
        List<GeologicalClass> geoClasses = sections.getGeologicalClasses();
        for (GeologicalClass geoClass : geoClasses) {
            // Exceptions
            if (geoClass.getName().equals(geoName)) {
                throw new UnprocException("! GeologicalClass witn this Name already exists in this Section!");
            }
            if (geoClass.getCode().equals(geoCode)) {
                throw new UnprocException("! GeologicalClass witn this Code already exists in this Section!");
            }
        } 
        // If geo is not exists, add to existing sec
        sections.addGeoClass(new GeologicalClass(sections, geoName, geoCode));
        sectionRepository.save(sections);
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
    /*
    *
    *  Non-optimal method, required separate class GeologicalClassRepository
    *
    public String findSectionsByGeologicalCode (String geoCode) {
        List<Section> neededSections = new ArrayList<>();
        Iterable<GeologicalClass> geoClasses = geoRepository.findAll();
        for (GeologicalClass geoClass : geoClasses) {
            if (geoClass.getCode().equals(geoCode)) {
                neededSections.add(geoClass.getSection());
            }
        }
        // Exeption
        if (neededSections.toString().equals("[]")) {
            throw new NotFoundException("! No any section found !");
        }
        // List to JSON format
        String output = "{";
        for (Section sec : neededSections) {
            output += findOneToJSON(sec.getId());
        }
        output += "}";
        return output;
    }
    */
}
