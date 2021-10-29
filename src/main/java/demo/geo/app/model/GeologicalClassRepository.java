package demo.geo.app.model;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import demo.geo.app.entities.GeologicalClass;

@Repository
public interface GeologicalClassRepository extends JpaRepository<GeologicalClass, Long> {

}

