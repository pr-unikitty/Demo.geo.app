package demo.geo.app.services;

import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

import java.util.*;

import demo.geo.app.entities.GeologicalClass;
import demo.geo.app.entities.Section;
import demo.geo.app.exceptions.NotFoundException;
import demo.geo.app.exceptions.UnprocessableException;
import demo.geo.app.dao.SectionRepository;

/**
 * Provides methods for {@link demo.geo.app.services.SectionService} management
 */
@Service
public class SectionServiceImpl implements SectionService {
    
    private final SectionRepository sectionRepository;
    
    private final String SECTION_NOT_FOUND_BY_ID = "Section with this ID does not exist";
    
    public SectionServiceImpl(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }
    
    /**
     * Finds all the {@link Section} and returns they as list
     * 
     * @return list of Section or null
     */
    @Override
    public List<Section> findAll() {
        return sectionRepository.findAll();
    }

    /**
     * Finds and returns {@link Section} by ID
     *
     * @param id id of section to find
     * @return section or null
     */
    @Override
    public Section findOne(long id) {
        Section section = sectionRepository.findById(id);
        if (section == null) {
            throw new NotFoundException(SECTION_NOT_FOUND_BY_ID);
        }
        return section;
    }
	
    /**
     * Adds new {@link Section} with list of {@link GeologicalClass} 
     * List can be empthy; each GeologicalClass must have unique name and code
     * or @throws UnprocessableException if Section not found
     * 
     * @param section Section to add
     * @return added Section
     */
    @Override
    @Transactional
    public Section createSection(Section section) {
        Section existingSection = sectionRepository.findByName(section.getName());
        if (existingSection != null) {
            throw new UnprocessableException("Section with this name is already exist");
        }
        Section newSection = new Section(section.getName());
        sectionRepository.save(newSection);
        sectionRepository.flush();
        
        List<GeologicalClass> geoClasses = section.getGeologicalClasses();
        if (geoClasses != null) {
            for (GeologicalClass newGeoClass : geoClasses) {
                addGeoClassIfAbsent(newSection, newGeoClass);
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
    @Override
    @Transactional
    public void deleteById(long id) {
        if (sectionRepository.findById(id) == null) {
            throw new NotFoundException(SECTION_NOT_FOUND_BY_ID);
        }
        sectionRepository.deleteById(id);
    }

    /**
     * Deletes all {@link Section}s
     * or @throws NotFoundException if no one Section is found
     */
    @Override
    @Transactional
    public void deleteAll() {
        List<Section> sections = sectionRepository.findAll();
        for (Section sec : sections) {
            sectionRepository.deleteById(sec.getId());
        }
    }
    
    /**
     * Adds {@link GeologicalClass} to existing {@link Section}
     * If name or code of GeologicalClass exists, @throws UnprocessableException
     * 
     * @param id id of Section to add GeologicalClass
     * @param geoClass GeologicalClass to add
     * @return updated Section
     */
    @Override
    @Transactional
    public Section addGeoclassOrThrow(long id, GeologicalClass geoClass) {
        Section existingSection = sectionRepository.findById(id);
        if (existingSection == null) {
            throw new NotFoundException(SECTION_NOT_FOUND_BY_ID);
        }
        if (addGeoClassIfAbsent(existingSection, geoClass)) {
            throw new UnprocessableException("GeologicalClass with these Name or Code already exists in this Section");
        }
        return sectionRepository.save(existingSection);
    }
    
    /**
     * TZ#2: Returns list of all {@link Section}s that have {@link GeologicalClass}es with the specified code
     * 
     * @param geoCode specified code of GeologicalClass
     * @return list of Section
     */
    @Override
    public List<Section> findSectionsByGeologicalCode (String geoCode) {
        return sectionRepository.findSectionsByGeoCode(geoCode);
    }

    /**
     * ! This method doesn't interact with repository !
     * 
     * Checks for the existence of a {@link GeologicalClass} with the same name 
     * or code in {@link Section} and adds GeologicalClass, if absent
     * 
     * @param section existing section to update
     * @param newGeoClass GeologicalClass to add in this section
     * @return true, if GeologicalClass with the same name or code is exists in
     * this section, orelse return false
     */
    @Override
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
        newGeoClass.setSectionId(section.getId());
        existingGeoClasses.add(newGeoClass);
        section.setGeologicalClasses(existingGeoClasses);
        return false;
    } 
}
