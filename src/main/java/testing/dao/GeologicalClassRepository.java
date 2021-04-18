package testing.dao;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface GeologicalClassRepository extends CrudRepository<GeologicalClass, Integer> {
    List<GeologicalClass> findByName(String name);
}