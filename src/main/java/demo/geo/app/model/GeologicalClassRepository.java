package demo.geo.app.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import demo.geo.app.entities.GeologicalClass;

@Repository
public interface GeologicalClassRepository extends JpaRepository<GeologicalClass, Long> {

}

