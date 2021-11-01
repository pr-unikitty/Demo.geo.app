package demo.geo.app.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import demo.geo.app.entities.GeologicalClass;
import demo.geo.app.entities.Section;
import demo.geo.app.exceptions.NotFoundException;
import demo.geo.app.exceptions.UnprocException;
import demo.geo.app.dao.SectionRepository;

@Service
public class SectionService {
    
    private final SectionRepository sectionRepository;
        
    @Autowired
    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }
    
    /**
     * Finds all the {@link Section} and returns they as list
     * 
     * @return list of Section or null
     */
    public List<Section> findAll() {
        return sectionRepository.findAll();
    }
    
    /**
     * Finds and returns {@link Section} by ID
     * 
     * @param id id of section to find
     * @return section or null
     */
    public Section findOne(long id) {
        return sectionRepository.findById(id).orElse(null);
    }
	
    /**
     * Adds new {@link Section} with list of {@link GeologicalClass} 
     * List can be empthy; each GeologicalClass must have unique name and code
     * or @throws UnprocException if Section not found
     * 
     * @param section Section to add
     * @return added Section
     */
    public Section createSection(Section section) {
        Section existingSection = sectionRepository.findByName(section.getName());
        if (existingSection != null) {
            throw new UnprocException("! Section with this name is already exist !");
        }
        Section newSection = new Section(section.getName());
        sectionRepository.save(newSection);
        sectionRepository.flush();
        
        List<GeologicalClass> geoClasses = section.getGeologicalClasses();
        if (geoClasses != null) {
            for (GeologicalClass newGeoClass : geoClasses) {
                addGeoclassOrThrow(newSection.getId(), newGeoClass);
            }
        }
        return sectionRepository.save(newSection);
    }
        
    /**
     * Deletes one {@link Section} by ID  
     * or @throws NotFoundException if Section not found
     * 
     * @param id id of Section to delete
     */
    public void deleteById(long id) {
        if (sectionRepository.getById(id) == null) {
            throw new NotFoundException("! Section with this ID can not be deleted because is not found !");
        }
        sectionRepository.deleteById(id);
    }

    // Delete all sections with geoClasses  

    /**
     * Deletes all {@link Section}s
     * or @throws NotFoundException if no one Section is found
     */
    public void deleteAll() {
        List<Section> sections = sectionRepository.findAll();
        if (sections.isEmpty()) {
            throw new NotFoundException("! No any section found (DB is empty) !");
        }
        for (Section sec : sections) {
            sectionRepository.deleteById(sec.getId());
        }
    }
    
    /**
     * Adds {@link GeologicalClass} to existing {@link Section}
     * If name or code of GeologicalClass exists, @throws UnprocException
     * 
     * @param id id of Section to add GeologicalClass
     * @param geoClass GeologicalClass to add
     * @return updated Section
     */
    public Section addGeoclassOrThrow(long id, GeologicalClass geoClass) {
        Section existingSection = sectionRepository.getById(id);
        if (existingSection == null) {
            throw new NotFoundException("! Section with this ID does not exists !");
        }
        if (addGeoClassIfAbsent(existingSection, geoClass)) {
            throw new UnprocException("! GeologicalClass witn this Name or this Code already exists in this Section!");
        }
        return sectionRepository.save(existingSection);
    }
    
    /**
     * TZ#2: Returns list of all {@link Section}s that have {@link GeologicalClass}es with the specified code
     * 
     * @param geoCode specified code of GeologicalClass
     * @return list of Section
     */
    public List<Section> findSectionsByGeologicalCode (String geoCode) {
        List<Section> neededSections = sectionRepository.findSectionsByGeoCode(geoCode);
        return neededSections;
    }

    /**
     * Checks for the existence of a {@link GeologicalClass} with the same name 
     * or code in {@link Section} and adds GeologicalClass, if absent
     * 
     * @param section existing section to update
     * @param newGeoClass GeologicalClass to add in this section
     * @return true, if GeologicalClass with the same name or code is exists in
     * this section, orelse return false
     */
    public boolean addGeoClassIfAbsent(Section section, GeologicalClass newGeoClass) {
        List<GeologicalClass> existingGeoClasses = section.getGeologicalClasses();
        if (!existingGeoClasses.isEmpty()) {
            for (GeologicalClass existingGeoClass : existingGeoClasses) {
                if (existingGeoClass.getName().equals(newGeoClass.getName()) || 
                        existingGeoClass.getCode().equals(newGeoClass.getCode())) {
                    return true;
                }
            }
        }
        existingGeoClasses.add(newGeoClass);
        newGeoClass.setSectionId(section.getId());
        section.setGeologicalClasses(existingGeoClasses);
        return false;
    } 
}
