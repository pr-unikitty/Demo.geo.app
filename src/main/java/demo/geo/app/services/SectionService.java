package demo.geo.app.services;

import java.util.*;

import demo.geo.app.entities.GeologicalClass;
import demo.geo.app.entities.Section;

public interface SectionService {
    
    List<Section> findAll();
    Section findOne(long id);
    Section createSection(Section section);
    void deleteById(long id);
    void deleteAll();
    Section addGeoclassOrThrow(long id, GeologicalClass geoClass);
    List<Section> findSectionsByGeologicalCode (String geoCode);
    boolean addGeoClassIfAbsent(Section section, GeologicalClass newGeoClass);
    
}
