package testing.dao;

import org.springframework.data.repository.CrudRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SectionRepository extends CrudRepository<Section, Integer> {
    @Query(value = "SELECT * FROM section sec JOIN geoclasses geo ON sec.section_id = geo.section_id WHERE geo.code=?1", nativeQuery = true) 
    List<Section> findSectionsByGeoCode(@Param("code") String code);
    
    List<Section> findByName(String name);
}

