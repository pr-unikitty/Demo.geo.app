package demo.geo.app.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import demo.geo.app.entities.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query(value = "SELECT * FROM sections sec JOIN geoclasses geo ON sec.section_id = geo.section_id WHERE geo.code=?1", nativeQuery = true) 
    List<Section> findSectionsByGeoCode(@Param("code") String code);
    Section findById(long id);
    Section findByName(String name);
    void deleteById(long id);
}

